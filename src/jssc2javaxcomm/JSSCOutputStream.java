package jssc2javaxcomm;

import java.io.IOException;
import java.io.OutputStream;
import jssc.SerialPort;

public class JSSCOutputStream extends OutputStream {

  private final JSSCSerialPort serialPort;

  public JSSCOutputStream(JSSCSerialPort serialPort) {
    this.serialPort=serialPort;
  }

  /**
   * Vnitřní procedura. Je zavedena z důvodu vláknové bezpečnosti a proto je
   * synchronized, tímto se vyhneme komplikovanému použití semaforů.
   *
   * @param mode co budeme tvořit
   * @param data parametr
   * @throws IOException
   */
  private synchronized void internalSynchronizedMultifunction(int mode, int data) throws IOException {
    try { //odchyt vyjimek
      switch (mode) {
	case 1: //purgePort
	  serialPort.sp.purgePort(data); //provedem pozadovane purge
	  break;
	default: //zapis
	  serialPort.sp.writeInt(data); //zapisem data na seriak
	  break;
      }
    } catch (Exception e) { //odlov vyjimek jscc
      throw (IOException) e; //tu prekabatime na IOException
    }
  }

  @Override
  public void write(int b) throws IOException {
    internalSynchronizedMultifunction(0, b); //zapiseme jeden bajt
  }

  @Override
  public void flush() throws IOException {
    internalSynchronizedMultifunction(1, SerialPort.PURGE_TXCLEAR); //purge
  }

  @Override
  public void close() throws IOException {
    serialPort.close(); //zavirame kram
  }
}
