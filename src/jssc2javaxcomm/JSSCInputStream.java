package jssc2javaxcomm;

import java.io.IOException;
import java.io.InputStream;

class JSSCInputStream extends InputStream {
  private final JSSCSerialPort serialPort;
  
  public JSSCInputStream(JSSCSerialPort serialPort) {
    this.serialPort=serialPort;
  }
  
  private synchronized int internalSynchronizedMultifunction(int mode) throws IOException {
    try { //odchyt vyjimek
      switch (mode) {
	case 1: //zjistujeme pocet bajtu v bufiku
	  return serialPort.sp.getInputBufferBytesCount();
	default: //cteni bajtu
	  if (serialPort.sp.getInputBufferBytesCount()>0) { //pokud neco mame
	    int[] x=serialPort.sp.readIntArray(1); //nabereme jedno dato jako integer (at se s tim nemusim prevadet jak trubka)
	    return x[0]; //a vracime vysledek
	  }	  
	  return -1; //nemame data
      }
    } catch (Exception e) { //odlov vyjimek jscc
      throw (IOException) e; //tu prekabatime na IOException
    }
  }  
  
  @Override
  public int available() throws IOException {
    return internalSynchronizedMultifunction(1); //cteni poctu bajtu v bufiku    
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public void close() throws IOException {
    serialPort.close(); //zavirame kram
  }

  @Override
  public int read() throws IOException {
    while (true) { //cteni ma byt blokujici, toz tedy budeme blokovat
      int r=internalSynchronizedMultifunction(0); //cteni bajtu
      if (r>=0) { //pokud neco mame
	return r; //tak ven s tim
      }
      Thread.yield(); //aktivne hodime rizeni jinemu vlaknu, tady se ceka :-(
    }
  }
}
