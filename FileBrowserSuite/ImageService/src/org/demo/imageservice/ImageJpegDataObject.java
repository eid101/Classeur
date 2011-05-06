/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.demo.imageservice;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.demo.fileservice.DocumentWriter;
import org.demo.fileservice.Thumbnail;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public class ImageJpegDataObject extends MultiDataObject implements Thumbnail,DocumentWriter {
    public static final String EXTENSION = "jpg";
    
    public ImageJpegDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);

    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    @Override
    public BufferedImage getThumbnail() {
        return ImageIOUtils.getImage(getPrimaryFile());
    }
    
    @Override
    public void write(BufferedImage source) {
        ImageIOUtils.writeImage(getPrimaryFile(), EXTENSION, source);
    }    
}
