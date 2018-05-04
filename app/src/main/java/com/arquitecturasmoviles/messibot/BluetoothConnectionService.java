package com.arquitecturasmoviles.messibot;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";

    private static final String appName = "MYAPP";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     Este thread se ejecuta mientras se escuchan las conexiones entrantes.
     Se comporta como un cliente del lado del servidor.
     Funciona hasta que se acepta una conexión(o hasta que se cancele).
     **/
    private class AcceptThread extends Thread {

        // El server socket local
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Crear un nuevo socket para servidor de escucha
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Configurando el Servidor usando: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread corriendo actualmente.");

            BluetoothSocket socket = null;

            try {
                // Esta es una llamada de bloqueo y sólo regresará si
                // la conexión se realiza correctamente o si se produce una excepción.
                Log.d(TAG, "run: Inicio de socket de servidor RFCOM.....");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: Conexión aceptada del socket del servidor RFCOM.");

            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            if (socket != null) {
                connected(socket, mmDevice);
            }

            Log.i(TAG, "Finalizar mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Cancelando AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Cierre de AcceptThread ServerSocket falló. " + e.getMessage());
            }
        }

    }
    /**
     * Este Thread se ejecuta mientras se intenta establecer una conexión saliente con un dispositivo.
     * Se ejecuta directamente; la conexión tiene éxito o falla.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: Iniciada.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "Ejecutar mConnectThread ");

            // Obtener un BluetoothSocket para una conexión con el BluetoothDevice dado
            try {
                Log.d(TAG, "ConnectThread: Intentando crear InsecureRfcommSocket usando UUID: "
                        + MY_UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: No se pudo crear InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Siempre cancele la detección porque ralentizará una conexión
            mBluetoothAdapter.cancelDiscovery();

            // Establezca una conexión con el BluetoothSocket

            try {
                // Esta es una llamada de bloqueo y sólo regresará
                // si la conexión se realiza correctamente o si se produce una excepción.
                mmSocket.connect();

                Log.d(TAG, "run: ConnectThread conectado.");
            } catch (IOException e) {
                // Cerrar the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Socket cerrado.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: No se puede cerrar la conexión en el socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: No se pudo conectar al UUID: " + MY_UUID_INSECURE);
            }

            connected(mmSocket, mmDevice);
        }
    }
    /**
     * AcceptThread arranca y se sienta a la espera de una conexión.
     * A continuación, ConnectThread se inicia e intenta establecer una conexión con los otros dispositivos AcceptThread.
     **/
    public void startClient(BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startClient: Started.");

        //dialogo inicial
        mProgressDialog = ProgressDialog.show(mContext, "Conectando Bluetooth"
                , "Por favor espere...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    /**
     * Finalmente el ConnectedThread es responsable de mantener la BTConnection,
     * enviar los datos y recibir los datos entrantes a través de flujos de entrada/salida respectivamente.
     **/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Iniciando.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //descartar el diálogo de progreso cuando se establece la conexión
            try {
                mProgressDialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Empezando.");

        // Iniciar el hilo para gestionar la conexión y realizar transmisiones
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }
}
