package jssc2javaxcomm;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import jssc.SerialPortException;

public class JSSCSerialPort extends SerialPort implements jssc.SerialPortEventListener {

  jssc.SerialPort sp;
  JSSCInputStream is=null;
  JSSCOutputStream os=null;
  private int flowControlMode;
  private int baudrate;
  private int dataBits;
  private int stopBits;
  private int parity;
  private boolean dtr;
  private boolean rts;
  private final ArrayList<SerialPortEventListener> spel=new ArrayList();
  private int receiveTimeout=0;
  private int receiveTreshold=0;
  private int receiveFraming=0;
  private int inputBufferSize=0;
  private int outputBufferSize=0;
  private int notifyFl=0;
  private static final int FL_notifyOnDataAvailable=0x01;
  private static final int FL_notifyOnOutputEmpty=0x02;
  private static final int FL_notifyOnCTS=0x04;
  private static final int FL_notifyOnDSR=0x08;
  private static final int FL_notifyOnRingIndicator=0x10;
  private static final int FL_notifyOnCarrierDetect=0x20;
  private static final int FL_notifyOnOverrunError=0x40;
  private static final int FL_notifyOnParityError=0x80;
  private static final int FL_notifyOnFramingError=0x100;
  private static final int FL_notifyOnBreakInterrupt=0x200;
  boolean mutex;

  JSSCSerialPort(String port) throws PortInUseException {
    sp=new jssc.SerialPort(port);    
    try {      
      sp.openPort();
      sp.addEventListener(this);
    } catch (SerialPortException ex) {
      throw new PortInUseException();
    }
  }
  
  @Override
  public void close() {
    lock();
    if (sp.isOpened()) {
      try {
	sp.closePort();
      } catch (SerialPortException ex) {
      }
    }    
    unlock();
  }  

  private synchronized boolean proc(boolean newState) {
    if (newState&&(mutex)) {
      return false; //pokud uz zamek byl, pak spatny
    }
    mutex=newState; //nastavime
    return true; //vracime uspech
  }

  private void lock() {
    while (!proc(true)) {
      Thread.yield(); //dokud ho nenahodis, tak makej
    }
  }

  private void unlock() {
    proc(false);
  }

  private void setFl(int mask, boolean value) {
    if (value) {
      notifyFl=mask;
    } else {
      notifyFl&=~mask;
    }
  }

  @Override
  public void setSerialPortParams(int baudrate, int dataBits, int stopBits, int parity) throws UnsupportedCommOperationException {
    lock();
    this.baudrate=baudrate;
    this.dataBits=dataBits;
    this.stopBits=stopBits;
    this.parity=parity;
    switch (dataBits) {
      case DATABITS_5:
	dataBits=jssc.SerialPort.DATABITS_5;
	break;
      case DATABITS_6:
	dataBits=jssc.SerialPort.DATABITS_6;
	break;
      case DATABITS_7:
	dataBits=jssc.SerialPort.DATABITS_7;
	break;
      case DATABITS_8:
	dataBits=jssc.SerialPort.DATABITS_8;
	break;
      default:
	unlock();
	throw new UnsupportedCommOperationException();
    }
    switch (stopBits) {
      case STOPBITS_1:
	stopBits=jssc.SerialPort.STOPBITS_1;
	break;
      case STOPBITS_1_5:
	stopBits=jssc.SerialPort.STOPBITS_1_5;
	break;
      case STOPBITS_2:
	stopBits=jssc.SerialPort.STOPBITS_2;
	break;
      default:
	unlock();
	throw new UnsupportedCommOperationException();
    }
    switch (parity) {
      case PARITY_EVEN:
	parity=jssc.SerialPort.PARITY_EVEN;
	break;
      case PARITY_MARK:
	parity=jssc.SerialPort.PARITY_MARK;
	break;
      case PARITY_NONE:
	parity=jssc.SerialPort.PARITY_NONE;
	break;
      case PARITY_ODD:
	parity=jssc.SerialPort.PARITY_ODD;
	break;
      case PARITY_SPACE:
	parity=jssc.SerialPort.PARITY_SPACE;
	break;
      default:
	unlock();
	throw new UnsupportedCommOperationException();
    }
    try {
      sp.setParams(baudrate, dataBits, stopBits, parity);
    } catch (SerialPortException ex) {
      unlock();
      throw new UnsupportedCommOperationException();
    }
    unlock();
  }

  @Override
  public int getBaudRate() {
    return baudrate;
  }

  @Override
  public int getDataBits() {
    return dataBits;
  }

  @Override
  public int getStopBits() {
    return stopBits;
  }

  @Override
  public int getParity() {
    return parity;
  }

  @Override
  public int getFlowControlMode() {
    return flowControlMode;
  }

  @Override
  public void setFlowControlMode(int flowcontrol) throws UnsupportedCommOperationException {
    lock();
    flowControlMode=flowcontrol;
    int jsscfcm=0;
    jsscfcm|=((flowcontrol&FLOWCONTROL_RTSCTS_IN)!=0)?jssc.SerialPort.FLOWCONTROL_RTSCTS_IN:0;
    jsscfcm|=((flowcontrol&FLOWCONTROL_RTSCTS_OUT)!=0)?jssc.SerialPort.FLOWCONTROL_RTSCTS_OUT:0;
    jsscfcm|=((flowcontrol&FLOWCONTROL_XONXOFF_IN)!=0)?jssc.SerialPort.FLOWCONTROL_XONXOFF_IN:0;
    jsscfcm|=((flowcontrol&FLOWCONTROL_XONXOFF_OUT)!=0)?jssc.SerialPort.FLOWCONTROL_XONXOFF_OUT:0;
    try {
      sp.setFlowControlMode(jsscfcm);
    } catch (Exception e) {
      unlock();
      throw (UnsupportedCommOperationException) e;
    }
    unlock();
  }

  @Override
  public boolean isDTR() {
    return dtr;
  }

  @Override
  public void setDTR(boolean state) {
    try {
      sp.setDTR(state);
      dtr=state;
    } catch (SerialPortException ex) {
    }
  }

  @Override
  public void setRTS(boolean state) {
    try {
      sp.setRTS(state);
      rts=state;
    } catch (SerialPortException ex) {
    }
  }

  @Override
  public boolean isCTS() {
    try {
      return sp.isCTS();
    } catch (SerialPortException ex) {
      return false;
    }
  }

  @Override
  public boolean isDSR() {
    try {
      return sp.isDSR();
    } catch (SerialPortException ex) {
      return false;
    }
  }

  @Override
  public boolean isCD() {
    throw new UnsupportedOperationException("isCD() Not supported.");
  }

  @Override
  public boolean isRI() {
    try {
      return sp.isRING();
    } catch (SerialPortException ex) {
      return false;
    }
  }

  @Override
  public boolean isRTS() {
    return rts;
  }

  @Override
  public void sendBreak(int duration) {
    try {
      sp.sendBreak(duration);
    } catch (SerialPortException ex) {
    }
  }

  @Override
  public void addEventListener(SerialPortEventListener lsnr) throws TooManyListenersException {
    lock();
    spel.add(lsnr);
    unlock();
  }

  @Override
  public void removeEventListener() {
    lock();
    spel.clear();
    unlock();
  }

  @Override
  public void notifyOnDataAvailable(boolean enable) {
    setFl(FL_notifyOnDataAvailable, enable);
  }

  @Override
  public void notifyOnOutputEmpty(boolean enable) {
    setFl(FL_notifyOnOutputEmpty, enable);
  }

  @Override
  public void notifyOnCTS(boolean enable) {
    setFl(FL_notifyOnCTS, enable);
  }

  @Override
  public void notifyOnDSR(boolean enable) {
    setFl(FL_notifyOnDSR, enable);
  }

  @Override
  public void notifyOnRingIndicator(boolean enable) {
    setFl(FL_notifyOnRingIndicator, enable);
  }

  @Override
  public void notifyOnCarrierDetect(boolean enable) {
    setFl(FL_notifyOnCarrierDetect, enable);
  }

  @Override
  public void notifyOnOverrunError(boolean enable) {
    setFl(FL_notifyOnOverrunError, enable);
  }

  @Override
  public void notifyOnParityError(boolean enable) {
    setFl(FL_notifyOnParityError, enable);
  }

  @Override
  public void notifyOnFramingError(boolean enable) {
    setFl(FL_notifyOnFramingError, enable);
  }

  @Override
  public void notifyOnBreakInterrupt(boolean enable) {
    setFl(FL_notifyOnBreakInterrupt, enable);
  }

  @Override
  public void enableReceiveFraming(int f) throws UnsupportedCommOperationException {
    if (f>=0) {
      receiveFraming=f;
    }
  }

  @Override
  public void disableReceiveFraming() {
    receiveFraming=0;
  }

  @Override
  public boolean isReceiveFramingEnabled() {
    return receiveFraming>0;
  }

  @Override
  public int getReceiveFramingByte() {
    return receiveFraming;
  }

  @Override
  public void disableReceiveTimeout() {
    receiveTimeout=-1;
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void enableReceiveTimeout(int time) throws UnsupportedCommOperationException {
    if (time>0) {
      receiveTimeout=time;
      return;
    }
    throw new UnsupportedCommOperationException();
  }

  @Override
  public boolean isReceiveTimeoutEnabled() {
    return (receiveTimeout>0);
  }

  @Override
  public int getReceiveTimeout() {
    return receiveTimeout;
  }

  @Override
  public void enableReceiveThreshold(int thresh) throws UnsupportedCommOperationException {
    if (thresh>=0) {
      receiveTreshold=thresh;
      return;
    }
    throw new UnsupportedCommOperationException();
  }

  @Override
  public void disableReceiveThreshold() {
    receiveTreshold=0;
  }

  @Override
  public int getReceiveThreshold() {
    return receiveTreshold;
  }

  @Override
  public boolean isReceiveThresholdEnabled() {
    return receiveTreshold>0;
  }

  @Override
  public void setInputBufferSize(int size) {
    if (size>0) {
      inputBufferSize=size;
    }
  }

  @Override
  public int getInputBufferSize() {
    return inputBufferSize;

  }

  @Override
  public void setOutputBufferSize(int size) {
    if (size>0) {
      outputBufferSize=size;
    }
  }

  @Override
  public int getOutputBufferSize() {
    return outputBufferSize;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    lock();
    if (this.is==null) {
      is=new JSSCInputStream(this);
    }
    unlock();
    return is;
  }

  @Override
  public OutputStream getOutputStream() throws IOException {
    lock();
    if (os==null) {
      os=new JSSCOutputStream(this);
    }
    unlock();
    return os;
  }

  private SerialPortEvent mkse(int type, int mask, boolean nv) {
    return ((notifyFl&mask)==0)?null:new SerialPortEvent(this, type, nv, !nv);
  }

  private SerialPortEvent mkse(int type, int mask) {
    return mkse(type, mask, false);
  }

  @Override
  public void serialEvent(jssc.SerialPortEvent jsscEvent) {
    lock();	    
    SerialPortEvent se=null;
    switch (jsscEvent.getEventType()) {
      case jssc.SerialPortEvent.CTS:
	try {
	  se=mkse(SerialPortEvent.CTS, FL_notifyOnCTS, sp.isCTS());
	} catch (SerialPortException ex) {
	  se=mkse(SerialPortEvent.CTS, FL_notifyOnCTS);
	}
	break;
      case jssc.SerialPortEvent.DSR:
	try {
	  se=mkse(SerialPortEvent.DSR, FL_notifyOnDSR, sp.isDSR());
	} catch (SerialPortException ex) {
	  se=mkse(SerialPortEvent.DSR, FL_notifyOnDSR);
	}
	break;
      case jssc.SerialPortEvent.RING:
	try {
	  se=mkse(SerialPortEvent.RI, FL_notifyOnRingIndicator, sp.isRING());
	} catch (SerialPortException ex) {
	  se=mkse(SerialPortEvent.RI, FL_notifyOnRingIndicator);
	}
	break;
      case jssc.SerialPortEvent.BREAK:
	se=mkse(SerialPortEvent.BI, FL_notifyOnBreakInterrupt);
	break;
      case jssc.SerialPortEvent.ERR:
	se=mkse(SerialPortEvent.FE, FL_notifyOnFramingError);
	break;
      case jssc.SerialPortEvent.RXCHAR:
	se=mkse(SerialPortEvent.DATA_AVAILABLE, FL_notifyOnDataAvailable);
	break;
      case jssc.SerialPortEvent.TXEMPTY:
	se=mkse(SerialPortEvent.OUTPUT_BUFFER_EMPTY, FL_notifyOnOutputEmpty);
	break;
      case jssc.SerialPortEvent.RXFLAG:
      case jssc.SerialPortEvent.RLSD:
      default:
	break;
    }
    if (se!=null) {
      for (SerialPortEventListener l : spel) {
	l.serialEvent(se);
      }
    }
    unlock();
  }
  
  private byte endOfInput=0;

  @Override
  public void setEndOfInputChar(byte data) {
    endOfInput=data;    
  }
}
