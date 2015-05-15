package gnu.io;

import java.util.*;

public interface SerialPortEventListener extends EventListener {

  public abstract void serialEvent(SerialPortEvent ev);
}
