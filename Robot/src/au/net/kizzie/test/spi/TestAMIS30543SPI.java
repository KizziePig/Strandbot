package au.net.kizzie.test.spi;


import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

import java.io.IOException;

/**
 * Running Stepper motor with AMIS30435
 * CS0 and CS1 (chip-select) are supported for SPI0.
 *
 * NOTE: make sure the SPI is enabled on the Raspberry Pi via the raspi-config utility under the Interfaces menu option.
 * 
 * NOTE: The WiringPi pinouts are used not the BCM pinouts found in internet diagrams. 
 pi@raspberrypi:~ $ gpio readall
 +-----+-----+---------+------+---+---Pi 3---+---+------+---------+-----+-----+
 | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
 |     |     |    3.3v |      |   |  1 || 2  |   |      | 5v      |     |     |
 |   2 |   8 |   SDA.1 |   IN | 1 |  3 || 4  |   |      | 5v      |     |     |
 |   3 |   9 |   SCL.1 |   IN | 1 |  5 || 6  |   |      | 0v      |     |     |
 |   4 |   7 | GPIO. 7 |   IN | 1 |  7 || 8  | 0 | IN   | TxD     | 15  | 14  |
 |     |     |      0v |      |   |  9 || 10 | 1 | IN   | RxD     | 16  | 15  |
 |  17 |   0 | GPIO. 0 |   IN | 0 | 11 || 12 | 0 | ALT0 | GPIO. 1 | 1   | 18  |
 |  27 |   2 | GPIO. 2 |   IN | 0 | 13 || 14 |   |      | 0v      |     |     |
 |  22 |   3 | GPIO. 3 |   IN | 0 | 15 || 16 | 0 | IN   | GPIO. 4 | 4   | 23  |
 |     |     |    3.3v |      |   | 17 || 18 | 0 | IN   | GPIO. 5 | 5   | 24  |
 |  10 |  12 |    MOSI | ALT0 | 0 | 19 || 20 |   |      | 0v      |     |     |
 |   9 |  13 |    MISO | ALT0 | 0 | 21 || 22 | 0 | IN   | GPIO. 6 | 6   | 25  |
 |  11 |  14 |    SCLK | ALT0 | 0 | 23 || 24 | 1 | OUT  | CE0     | 10  | 8   |
 |     |     |      0v |      |   | 25 || 26 | 1 | OUT  | CE1     | 11  | 7   |
 |   0 |  30 |   SDA.0 |   IN | 1 | 27 || 28 | 1 | IN   | SCL.0   | 31  | 1   |
 |   5 |  21 | GPIO.21 |   IN | 1 | 29 || 30 |   |      | 0v      |     |     |
 |   6 |  22 | GPIO.22 |   IN | 1 | 31 || 32 | 0 | IN   | GPIO.26 | 26  | 12  |
 |  13 |  23 | GPIO.23 |   IN | 0 | 33 || 34 |   |      | 0v      |     |     |
 |  19 |  24 | GPIO.24 | ALT0 | 0 | 35 || 36 | 0 | IN   | GPIO.27 | 27  | 16  |
 |  26 |  25 | GPIO.25 |   IN | 0 | 37 || 38 | 0 | ALT0 | GPIO.28 | 28  | 20  |
 |     |     |      0v |      |   | 39 || 40 | 0 | ALT0 | GPIO.29 | 29  | 21  |
 +-----+-----+---------+------+---+----++----+---+------+---------+-----+-----+
 | BCM | wPi |   Name  | Mode | V | Physical | V | Mode | Name    | wPi | BCM |
 +-----+-----+---------+------+---+---Pi 3---+---+------+---------+-----+-----+
 * 
 * Connect Pi physical 4 to the IOREF line and physical pin 6 to the GND line below the IOREF.
 * Connect white from physical pin 18 (GPIO 5) to NXT(STEP) line
 * Connect grey from physical pin  16 (GPIO 4) to DIR line
 * Connect purple from physical pin 21 (SPI0 MISO) to DO line
 * Connect blue from physical pin 19 (SPI0 MOSI) to DI line
 * Connect green from physical pin 23 (SPI0 SCLK) to CLK line
 * Connect yellow from physical pin 24- (SPI0 CE0) to CS line
 * Connect the motor main power to VMOT and GND and the 4 stepper motor wires red, blue, black, green to MXP, MXN, MYN, MYP respectively
 * Connect black from physical pin 22 (GPIO 6) to ERR line
 * Connect orange from physical pin 13 (GPIO 2) to CLR line
 */
public class TestAMIS30543SPI {
    public static void writeRegister(SpiDevice spi, int address, byte data) throws IOException {
        byte firstByte = (byte) (0b10000000 | address);
        System.out.print("writeRegister: firstByte "+byteToBinaryString(firstByte)+" data "+byteToBinaryString(data)+" ");
        
        byte[] packet = new byte[] {
            firstByte,
            data
        };

        // send conversion request to ADC chip via SPI channel
        byte[] result = spi.write(packet);

        System.out.print(": result (length "+result.length+") ");
        for (int i = 0; i < result.length; i++) {
            System.out.print(byteToBinaryString(result[i])+" ");
        }
        System.out.println();
    }

    public static byte readRegister(SpiDevice spi, int address, boolean displayOutput) throws IOException {
        byte firstByte = (byte) (0b00000000 | address);
        if (displayOutput) System.out.print("readRegister: firstByte "+byteToBinaryString(firstByte)+" ");
        
        byte[] packet = new byte[] {
            firstByte,
            (byte) 0b00000000       // data
        };

        // send conversion request to ADC chip via SPI channel
        byte[] result = spi.write(packet);

        if (displayOutput) System.out.println(": result "+byteToBinaryString(result[1]));
        return result[1];
    }

    public static String byteToBinaryString(byte b) {
        return String.format("%8s",Integer.toBinaryString(b&0xFF)).replace(' ','0');
    }
    
    private SpiDevice spi = null;
    final GpioController gpio = GpioFactory.getInstance();
    final GpioPinDigitalOutput stepOutputPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW); 
    final GpioPinDigitalOutput dirOutputPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW); 
    final GpioPinDigitalOutput clrOutputPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW); 
    final GpioPinDigitalInput errInputPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06);

    public TestAMIS30543SPI() throws IOException {
        spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
    }

    public void initialiseControlRegisters() throws IOException, InterruptedException {
        System.out.println("initialiseControlRegisters: Writing control registers");
        // Reset chip
        gpio.setState(PinState.HIGH, clrOutputPin); 
        Thread.sleep(1);                                // Line needs to be high for 100 micro seconds
        gpio.setState(PinState.LOW, clrOutputPin);

        // WR Address 0 is WDEN | WDT[3:0] | - | - | - where WDEN is Watch Dog Enable and WDT is the Watch Dog Timeout Interval -- see datasheet
        writeRegister(spi,0,(byte) 0b00000000);
        
        // CR0 Address 1 is SM[2:0] | CUR[4:0] where SM is the step mode (see datasheet Table 9): the default is 000 for 1/32 and for 1/128 SM[2:0] is ignored and ESM[2:0] is set to 001
        //                                           CUR[4:0] is the current limt (see datasheet Table 13): 01100 for 955mA; 11000 for 2845mA; 10111 for 2700mA
        writeRegister(spi,1,(byte) 0b11101100);
        
        // CR1 Address 2 is DIRCTRL | NXTP | - | - | PWMF | PWMJ | EMC[1:0] where DIRCTRL is direction control - set to 0 implies DIR = 0 for CW and DIR = 1 for CCW
        //                                                                        NXTP is 0 for NXT on rising edge; 1 for NXT on falling edge
        //                                                                        PWMF enables doubling the PWM frequency
        //                                                                        PWMJ allows jittery PWM
        //                                                                        EMC[1:0] alters slopes of motor driver: 00 for very fast down to 11 for very slow
        writeRegister(spi,2,(byte) 0b00000000);
        
        // CR2 Address 3 is MOTEN | SLP | SLAG | SLAT | - | - | - | - where MOTEN = 1 activates the motor driver outputs
        //                                                                  SLP enables sleep mode
        //                                                                  SLAG adjusts speed load angle gain
        //                                                                  SLAT is the speed load angle transparency but
        writeRegister(spi,3,(byte) 0b10000000);
        
        // CR3 Address 9 is - | - | - | - | - | ESM[2:0] where ESM[2:0] is the step mode: 001 for 1/128; 010 is 1/64th; 011 is compensated full step 2 phase on; 
        //                                                                                100 is compensated full step 1 phase on; 
        //                                                                                and anything else means stepping mode is defined by SM[2:0]
        writeRegister(spi,9,(byte) 0b00000000);
        
    }
    public void move() throws  InterruptedException , IOException {       
        // this will ensure that the motor is stopped when the program terminates
        gpio.setShutdownOptions(true, PinState.LOW, stepOutputPin);
        long sleepTime = 1;
        for (int i = 0; i < 1000; i++) {
            System.out.print(i+" ");
            readMSP();
            gpio.setState(PinState.LOW, stepOutputPin); // White Step/Next
            Thread.sleep(sleepTime);
            gpio.setState(PinState.HIGH, stepOutputPin); // White Step/Next
            Thread.sleep(sleepTime);
            // Test error line
            PinState errState = gpio.getState(errInputPin);
            /*if (PinState.LOW.equals(errState)) {
                System.out.println("move: Error occurred. Status registers are ...");
                readStatusRegisters();
                System.out.println("move: Stopping move");
                break;
            }*/
        }
    	//gpio.setState(PinState.LOW, pins1[1]); // Grey Dir
        /*
        System.out.println("Starting loop");
    	for (int i = 0; i < 10; i++) {
  //      while(true) {
    		gpio.setState(PinState.HIGH, pins1[0]);
    		//gpio.setState(PinState.HIGH, pins1[1]);
    		Thread.sleep(3);
    		gpio.setState(PinState.LOW, pins1[0]);
    		//gpio.setState(PinState.LOW, pins1[1]);
	    	Thread.sleep(3);
	    	Thread.sleep(500); // Speed control
                //System.out.println("At end of loop");
	}*/
        System.out.println("move: Finished");
    }

    public void disableMotorOutput() throws IOException, InterruptedException {
        System.out.println("disableMotorOutput: Writing control registers");
        
        // CR2 Address 3 is MOTEN | SLP | SLAG | SLAT | - | - | - | - where MOTEN = 1 activates the motor driver outputs
        //                                                                  SLP enables sleep mode
        //                                                                  SLAG adjusts speed load angle gain
        //                                                                  SLAT is the speed load angle transparency but
        writeRegister(spi,3,(byte) 0b00000000);
        
    }
    public void readControlRegisters() throws IOException {
        System.out.println("readControlRegisters: Reading control registers");
        readRegister(spi,0, true);
        readRegister(spi,1, true);
        readRegister(spi,2, true);
        readRegister(spi,3, true);
        readRegister(spi,9, true);
    }

    public void readStatusRegisters() throws IOException {
        System.out.println("readStatusRegisters: Reading status registers");
        readRegister(spi,4, true);
        readRegister(spi,5, true);
        readRegister(spi,6, true);
        readRegister(spi,7, true);
        readRegister(spi,10, true);
    }

    public void readMSP() throws IOException {
        byte msp8_2 = readRegister(spi,7,false);
        byte msp6_0 = readRegister(spi,10,false);
        String msp = byteToBinaryString(msp8_2).substring(1) + byteToBinaryString(msp6_0).substring(6);
        System.out.println("readMSP: msp "+msp/*+" from msp8_2 "+byteToBinaryString(msp8_2)+" and msp6_0 "+byteToBinaryString(msp6_0)*/);
    }
    

    public static void main(String args[]) throws InterruptedException, IOException {

        System.out.println("SPI to AMIS-30543");
        TestAMIS30543SPI motor = new TestAMIS30543SPI();
        motor.initialiseControlRegisters();
        motor.readControlRegisters();
        motor.readStatusRegisters();
             
        motor.move();
        
        motor.disableMotorOutput();
        motor.readStatusRegisters();
        System.out.println("Finished");
    }
}