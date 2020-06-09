package co.intentservice.chatui.sample


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.intentservice.chatui.models.ChatMessage
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException
import java.util.*


class LoraHandler : LifecycleService() {

    // stuff for connecting to USB device
    var connection: UsbDeviceConnection? = null
    var port: UsbSerialPort? = null
    private var usb_receiver: USBReceiver? = null

    // stuff for updating database file
    val WRITE_WAIT_MILLIS = 100

    private var mTimer: Timer? = null
    var mRunnable: Runnable? = null
    private val mHandler: Handler = Handler()

    // notification stuff
    var channelID: String? = null
    var pendingIntent: PendingIntent? = null
    var pendingIntentNewMessages: PendingIntent? = null
    var channelIdNewMessages = "campchat_notif_channel"
    var managerNewMessages: NotificationManager? = null

    private val mMessageRepository = MessageRepository(application)


    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate()
    {
        super.onCreate()
        if (mTimer != null) { // Cancel if already existed
            mTimer?.cancel()
        }
        else {
            mTimer = Timer() //recreate new
        }
        mTimer?.scheduleAtFixedRate(
                TimeDisplay(),
                0,
                1000) //Schedule task

        Log.e("service", "timer made")




        // Set up the notification to keep the LoRA monitoring in the foreground (doesn't get eaten)
        val notificationIntent = Intent(this, MainActivity::class.java)

        Log.e("service", "notif intent made")
        pendingIntent = PendingIntent.getActivity(
                this, 0,
                notificationIntent, 0
        )

        var channelId = "campchat_service_channel"

        val channelName = "campchat service channel"
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW)
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_LOW
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(chan)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("campchat")
                .setContentText("listening for messages...")
                .setSmallIcon(R.mipmap.icon)
                .setContentIntent(pendingIntent).build()

        startForeground(1, notification)


        // Set up the notification of new messages
        val notificationIntentNewMessages = Intent(this, MainActivity::class.java)

        pendingIntentNewMessages = PendingIntent.getActivity(
                this, 0,
                notificationIntentNewMessages, 0
        )



        val channelNameNewMessages = "campchat notification channel"
        val chanNewMessages = NotificationChannel(channelIdNewMessages,
                channelNameNewMessages, NotificationManager.IMPORTANCE_HIGH)
        chanNewMessages.lightColor = Color.BLUE
        chanNewMessages.importance = NotificationManager.IMPORTANCE_HIGH
        chanNewMessages.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        managerNewMessages = getSystemService(NotificationManager::class.java)
        managerNewMessages!!.createNotificationChannel(chanNewMessages)



        // send latest message observer
        mMessageRepository.allMessages.observe(this, androidx.lifecycle.Observer<List<ChatMessage>> { messages: List<ChatMessage>? ->

            if (port != null) {
                if(messages?.last()?.type == ChatMessage.Type.SENT) {
                    port?.write(messages?.last()?.message?.toByteArray(), WRITE_WAIT_MILLIS)
                    port?.purgeHwBuffers(true, true)
                }
            }
        })



        // Set up the USB connection
        val filter = IntentFilter()
        filter.addAction(ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(ACTION_USB_DEVICE_DETACHED)
        usb_receiver = USBReceiver()
        registerReceiver(usb_receiver, filter)


        setupUSBDevice()
    }

    fun setupUSBDevice()
    {
        val manager =
                getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            return
        }


        val driver = availableDrivers[0]

        connection = manager.openDevice(driver.device)
                ?: return


        port = driver.ports[0] // Most devices have just one port (port 0)

        port?.open(connection)
        port?.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        PortOpened()
        Log.e("filter", "usb device attached")
        Toast.makeText(applicationContext, "Dongle Attached", Toast.LENGTH_SHORT).show()
    }

    internal inner class USBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            Log.e("usb reciever", "entered")
            if (ACTION_USB_DEVICE_ATTACHED == action) {
                setupUSBDevice()

            }
            else if  (ACTION_USB_DEVICE_DETACHED == action){
                Log.d("filter", "usb device detached")
                Toast.makeText(applicationContext, "Dongle Detached", Toast.LENGTH_SHORT).show()
                port = null
                connection!!.close()
                PortClosed()
            }
        }
    }



    private fun PortOpened() {
        val intent = Intent("portCreated")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    private fun PortClosed() {
        port = null
        val intent = Intent("portClosed")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        port = null
        if(connection != null) {
            connection!!.close()
        }
        if(usb_receiver != null) {
            unregisterReceiver(usb_receiver)
        }

        mTimer?.cancel() //For Cancel Timer
        stopForeground(true)
    }



    //class TimeDisplay for handling receiving messages from LoRA
    internal inner class TimeDisplay : TimerTask() {



        fun TimeDisplay()
        {
            // Notifications of new messages


        }

        override fun run() { // run on another thread

            mRunnable = Runnable {
                var recvarr = ByteArray(1000)
                var recvstr = ""
                var len:Int? = 0
                try {
                    len = port?.read(recvarr, WRITE_WAIT_MILLIS)
                }
                catch (e: IOException)
                {
//                    Log.e("port" ,"not open")
                    PortClosed()
                    return@Runnable
                }

                if (len == null)
                {
//                    Log.e("port" ,"not open")
                    PortClosed()
                    return@Runnable
                }
                Log.e("lora", len.toString())


                while (len!! > 0) {
                    recvstr += String(recvarr.take(1000).toByteArray())
                    var len2 = port?.read(recvarr, WRITE_WAIT_MILLIS)
                    if (len2!! == 0) {
                        break
                    }
                    len += len2
                }
                // write the message to the database
                if (len!! > 0) {
                    mMessageRepository.insert(ChatMessage(recvstr, System.currentTimeMillis(), ChatMessage.Type.RECEIVED, "Them"))
                    val notification: Notification = NotificationCompat.Builder(applicationContext, channelIdNewMessages)
                            .setContentTitle("new campchat message")
                            .setContentText(recvstr)
                            .setSmallIcon(R.mipmap.icon)
                            .setContentIntent(pendingIntent).build()
                    managerNewMessages!!.notify(1234, notification)
                }

            }
            mHandler.post(mRunnable)

        }
    }


}
