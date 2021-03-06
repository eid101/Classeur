/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.demo.twainservice;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import org.demo.scannerservice.Parameters;
import org.demo.scannerservice.ScannerManager;
import org.demo.scannerservice.ScannerListener;
import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata.Type;
import uk.co.mmscomputing.device.twain.TwainIdentity;

/**
 *
 * @author edwin
 */
public class TwainManager implements ScannerManager, uk.co.mmscomputing.device.scanner.ScannerListener {

    private static Scanner scanner = null;
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public void addListener(ScannerListener listener) {
        listeners.add(ScannerListener.class, listener);
    }

    @Override
    public void removeListener(ScannerListener listener) {
        listeners.remove(ScannerListener.class, listener);
    }

    @Override
    public void acquire() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                if (scanner == null) {
                    scanner = Scanner.getDevice();
                    scanner.addListener(TwainManager.this);
                }
                try {
                    scanner.select();
                    scanner.acquire();
                } catch (ScannerIOException ex) {
                    Logger.getLogger(TwainManager.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        t.start();
    }

    protected void fireScanPerformed(BufferedImage img) {
        final ScannerListener[] list = listeners.getListeners(ScannerListener.class);
        for (ScannerListener l : list) {
            l.imageAcquired(img);
        }
    }

    @Override
    public void setConfiguration(Parameters cfg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Parameters getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Type type, ScannerIOMetadata metadata) {
        try {
            if (type.equals(ScannerIOMetadata.ACQUIRED)) {
                BufferedImage image = metadata.getImage();
                fireScanPerformed(image);
            } else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
                System.out.println(metadata.getStateStr());
            } else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
                metadata.getException().printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "mmsComputing jTwain";
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public Collection<org.demo.scannerservice.Scanner> getListDevices() {
        Collection<org.demo.scannerservice.Scanner> result = new ArrayList<org.demo.scannerservice.Scanner>();
            
            uk.co.mmscomputing.device.twain.TwainScanner twainMgr=(uk.co.mmscomputing.device.twain.TwainScanner) Scanner.getDevice();
            TwainIdentity[] identities = twainMgr.getIdentities();
            
            for (TwainIdentity identity : identities) {
                result.add(new TwainScanner(identity));
            }

        return result;
    }
}
