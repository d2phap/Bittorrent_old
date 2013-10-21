
import java.util.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Sự kiện tuỳ chỉnh
 * @author Duong Dieu Phap
 */

public interface CustomEventListener extends EventListener {
    
    public void onStart(CustomEventObject e);
    public void onOccur(CustomEventObject e);
    public void onFinish(CustomEventObject e);
}
