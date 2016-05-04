/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.byeNow;

/**
 *
 * @author lrodriguezn
 */
public class BeyNow extends Thread {
public ClearAll clear = new ClearAll();
public PackFast pack = new PackFast();
public SendAll send= new SendAll();
    public BeyNow() {
    }
@Override
    public void run(){
        clear.clearAll();
        pack.packFast();
        send.sendAll();
    }
}
