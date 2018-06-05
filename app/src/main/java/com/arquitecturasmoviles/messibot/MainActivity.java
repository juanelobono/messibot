package com.arquitecturasmoviles.messibot;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.UUID;

import static android.os.Build.*;


public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "MainActivity";

    private DrawerLayout mDraweLayout;
    private ActionBarDrawerToggle mToggle;

    private FirebaseAuth firebaseAuth;

    private static final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionService mBluetoothConnection;
    //Button btnStartConnection;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothDevice mBTDevice;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public DeviceListAdapter mDeviceListAdapter;

    ListView lvNewDevices;

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: llamado.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
//      broadcast de Permitir visibilidad: nunca se registra por eso no se puede desuscribir.
//      unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        } else {
            mDraweLayout = (DrawerLayout) findViewById(R.id.drawer);
            mToggle = new ActionBarDrawerToggle(this, mDraweLayout,R.string.open, R.string.close);
            mDraweLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(this);

            Switch swOnOffBluetooth = findViewById(R.id.swOnOffBluetooh);
            Button btnPlay = findViewById(R.id.btnPlay);

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: Validar conexion con equipo remoto para pasar a la activity de joystick
//                if(mBluetoothAdapter.getBondedDevices().size() > 0){
                    Intent intent = new Intent(MainActivity.this, JoystickActivity.class);
                    startActivity(intent);
//                }else{
//                    //El usuario cancela el permiso a habilitar el bluetooth.
//                    Toast.makeText(MainActivity.this, R.string.no_connected_device,
//                            Toast.LENGTH_LONG).show();
//                }
                }
            });

            lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
            mBTDevices = new ArrayList<>();

            //Broadcasts cuando cambia el estado del enlace
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver4, filter);

            registerReceiver(mBroadcastReceiver1,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            lvNewDevices.setOnItemClickListener(MainActivity.this);
            lvNewDevices.setEmptyView(findViewById(R.id.tvEmptyListViewBluetooth));

            swOnOffBluetooth.setChecked(mBluetoothAdapter.isEnabled());
            // Si el bluetooth esta activado al iniciar la app, busca los dispositivos cercanos de
            // forma predeterminada.
            if (mBluetoothAdapter.isEnabled()){
                discoverBTDevices();
            }


            swOnOffBluetooth.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if(isChecked){
                                Log.d(TAG, "enableDisableBT: Habilitando Bluetooth.");
                                enableBluetooth();
                            }else{
                                Log.d(TAG, "enableDisableBT: Deshabilitando Bluetooth.");
                                mBluetoothAdapter.disable();
                                if (mDeviceListAdapter != null){
                                    mDeviceListAdapter.clear();
                                    mDeviceListAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

            );

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Switch swOnOffBluetooth = findViewById(R.id.swOnOffBluetooh);
        switch (requestCode){
            case ENABLE_BLUETOOTH_REQUEST_CODE:
                if (resultCode == RESULT_CANCELED){
                    //El usuario cancela el permiso a habilitar el bluetooth.
                    Toast.makeText(MainActivity.this, R.string.enable_bluetooth_canceled,
                            Toast.LENGTH_LONG).show();
                    swOnOffBluetooth.setChecked(false);
                }
                if (resultCode == RESULT_OK){

                }
                break;
            default: super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void enableBluetooth(){
        // Si el estado es cambiado a ON desde el switch y no desde el SO.
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF){

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, ENABLE_BLUETOOTH_REQUEST_CODE );

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        final String userAdmin = "admin";
        final String passAdmin = "123";
        int id = item.getItemId();

        if(id == R.id.perfil){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
        if(id == R.id.admin){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.activity_admin_login, null);
            final EditText tiUsuarioAdmin = mView.findViewById(R.id.tiUsuarioAdmin);
            final EditText tiPassAdmin = mView.findViewById(R.id.tiPassAdmin);
            Button btnLoginAdmin = mView.findViewById(R.id.btnLoginAdmin);


            btnLoginAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(tiUsuarioAdmin.getText().toString().equals(userAdmin) && tiPassAdmin.getText().toString().equals(passAdmin)){
                        Toast.makeText(MainActivity.this, "Acceso satisfactorio",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Datos incorrectos",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
        }
        return false;
    }


    // Crea un BroadcastReceiver para ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Switch swOnOffBluetooth = (Switch) findViewById(R.id.swOnOffBluetooh);

            // Cuando la búsqueda encuentra un dispositivo
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        swOnOffBluetooth.setChecked(false);
                        if (mDeviceListAdapter != null){
                            mDeviceListAdapter.clear();
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        swOnOffBluetooth.setChecked(false);
                        if (mDeviceListAdapter != null){
                            mDeviceListAdapter.clear();
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        swOnOffBluetooth.setChecked(true);
                        discoverBTDevices();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        swOnOffBluetooth.setChecked(true);

                        break;
                }
            }
        }
    };
    /**
     * Broadcast Receiver para los cambios realizados en los estados de bluetooth, tales como:
     * 1) Modo de descubrimiento on/off.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Visibilidad Habilitada.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Visibilidad Inhabilitada. Capaz de recibir conexiones.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Visibilidad Inhabilitada. No es capaz de recibir conexiones.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Conectando....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Conectado.");
                        break;
                }

            }
        }
    };
    /**
     * Broadcast Receiver para listar dispositivos que aún no están emparejados
     * -Ejecutado por el método btnDiscover().
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);

            }
        }
    };
    /**
     * Broadcast Receiver que detecta cambios en el estado de los enlaces (cambios en el estado de emparejamiento)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 casos:
                //caso1: ya está enlazado
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //caso2: creación de un vínculo
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //caso3: romper un vínculo
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    //crear método para iniciar la conexión
    //la conexión fallará y la aplicación se bloqueará si no hiciste el emparejamiento primero
    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Inicialización de la conexión Bluetooth RFCOM.");

        mBluetoothConnection.startClient(device,uuid);
    }

    public void btnEnableDisable_Discoverable(View view) {
        Log.d(TAG, "btnEnableDisable_Discoverable: Hace al dispositivo visible por 300 segundos.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);

    }
    public void discoverBTDevices() {
        Log.d(TAG, "btnDiscover: Búsqueda de dispositivos no emparejados.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Cancelación del descubrimiento.");

            //Chequea permisos Bluetooth en el manifesto
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                checkBTPermissions();
            }

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            int versionAPI = VERSION.SDK_INT;

            //Chequea permisos Bluetooth en el manifesto
            if (versionAPI > 22){
                checkBTPermissions();
            }
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }
    /**
     Este método es necesario para todos los dispositivos que ejecutan API23+.
     Android debe comprobar programáticamente los permisos para bluetooth. Colocación de los permisos adecuados
     en el manifiesto no es suficiente.
     NOTA: Esto sólo se ejecutará en las versiones > LOLLIPOP porque de lo contrario no es necesario.
     */
    @RequiresApi(api = VERSION_CODES.M)
    private void checkBTPermissions() {
        if(VERSION.SDK_INT > VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No es necesario comprobar los permisos. Versión SDK < LOLLIPOP.");
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //primero cancela el descubrimiento porque es muy intensivo en memoria.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: Ha hecho click en un dispositivo.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //crear el vínculo.
        //NOTA: Requiere API 17 (JellyBean)
        if(VERSION.SDK_INT > VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Tratando de emparejarse con " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(MainActivity.this);
        }
    }
}