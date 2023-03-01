//使用方法:
//1.引入ble.js
//2.在相应按钮上添加StartBLE()调用方法
//3.任何时候读取ax, ay, az, wx, wy, wz, Roll, Pitch, Yaw数据
var ax = 0;			//加速度x
var ay = 0;			//加速度y
var az = 0;			//加速度z
var wx = 0;			//角速度x
var wy = 0;			//角速度y
var wz = 0;			//角速度z
var Roll = 0;		//角度x
var Pitch = 0;		//角度y
var Yaw = 0;		//角度z

StartBLE = async function() {
	let device = await navigator.bluetooth.requestDevice({
		filters: [{
			namePrefix: 'WT901BLE68', //陀螺仪
		}, {
			namePrefix: 'GR-94103', //电子尺
		}],
		// acceptAllDevices: true,
		optionalServices: ["0000ffe5-0000-1000-8000-00805f9a34fb"]
	});
	console.log(device);

	let server = await device.gatt.connect();
	let service = await server.getPrimaryService("0000ffe5-0000-1000-8000-00805f9a34fb");
	let characteristic = await service.getCharacteristic("0000ffe4-0000-1000-8000-00805f9a34fb");

	characteristic.addEventListener(
		'characteristicvaluechanged', e => {
			var data = e.target.value.buffer;
			var data = new Int8Array(data)
			// console.log(data);
			if (data.byteLength == 20 && data[0] == 0x55 && data[1] == 0x61) {
				// 加速度计算方法：单位 g g 为重力加速度，可取 9.8m/s2
				var k_acc = 16.0;
				ax = (((data[3] << 8) | data[2]) / 32768 * k_acc).toFixed(2);
				ay = (((data[5] << 8) | data[4]) / 32768 * k_acc).toFixed(2);
				az = (((data[7] << 8) | data[6]) / 32768 * k_acc).toFixed(2);
				if (ax >= k_acc) ax -= 2 * k_acc;
				if (ay >= k_acc) ay -= 2 * k_acc;
				if (az >= k_acc) az -= 2 * k_acc;
				// 角速度计算方法：单位 °/s
				k_gyro = 2000.0;
				wx = (((data[9] << 8) | data[8]) / 32768 * k_gyro).toFixed(2);
				wy = (((data[11] << 8) | data[10]) / 32768 * k_gyro).toFixed(2);
				wz = (((data[13] << 8) | data[12]) / 32768 * k_gyro).toFixed(2);
				if (wx >= k_gyro) wx -= 2 * k_gyro;
				if (wy >= k_gyro) wy -= 2 * k_gyro;
				if (wz >= k_gyro) wz -= 2 * k_gyro;
				// 角度计算方法：单位 °，分别是
				k_angle = 180.0;
				// 滚转角（x 轴）
				Roll = (((data[15] << 8) | data[14]) / 32768 * k_angle).toFixed(2);
				// 俯仰角（y 轴）
				Pitch = (((data[17] << 8) | data[16]) / 32768 * k_angle).toFixed(2);
				// 偏航角（z 轴）
				Yaw = (((data[19] << 8) | data[18]) / 32768 * k_angle).toFixed(2);
				if (Roll >= k_angle) Roll -= 2 * k_angle;
				if (Pitch >= k_angle) Pitch -= 2 * k_angle;
				if (Yaw >= k_angle) Yaw -= 2 * k_angle
				console.log('加速度：', ax, ay, az, '角速度：', wx, wy, wz, '角度：', Roll, Pitch, Yaw);
			}
		}
	);

	characteristic.startNotifications()
		.then(function(data) {
			console.log("开始接受数据");
			console.log(ax,wx,Roll,Pitch,Yaw);
		});

	// return {
	// 	"ax1": ax,
	// 	"wx1": wx,
	// 	"R": Roll,
	// 	"P": Pitch,
	// 	"Y": Yaw,
	// }
	// await characteristic.writeValue(
	// 	new Uint8Array([50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60])
	// );
}

endBle = async function (){
	let device = await navigator.bluetooth.requestDevice({
		filters: [{
			namePrefix: 'WT901BLE68', //陀螺仪
		}, {
			namePrefix: 'GR-94103', //电子尺
		}],
		// acceptAllDevices: true,
		optionalServices: ["0000ffe5-0000-1000-8000-00805f9a34fb"]
	});
	console.log(device);

	let server = await device.gatt.disconnect();

	ay = 0;
	ax = 0;
	az = 0;
	wx = 0;
	wy = 0;
	wz = 0;
	Pitch = 0;
	Roll = 0;
	Yaw = 0;
}
