package gnu.io;

import java.util.*;

public interface CommPortOwnershipListener extends EventListener {

  public static final int PORT_OWNED=1;
  public static final int PORT_UNOWNED=2;
  public static final int PORT_OWNERSHIP_REQUESTED=3;

  public abstract void ownershipChange(int type);
}
