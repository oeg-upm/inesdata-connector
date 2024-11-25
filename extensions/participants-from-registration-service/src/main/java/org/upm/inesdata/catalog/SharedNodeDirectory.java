package org.upm.inesdata.catalog;

import org.eclipse.edc.catalog.directory.InMemoryNodeDirectory;
import org.eclipse.edc.crawler.spi.TargetNode;
import org.eclipse.edc.crawler.spi.TargetNodeDirectory;

import java.util.List;

public class SharedNodeDirectory implements TargetNodeDirectory {

    private volatile InMemoryNodeDirectory nodeDirectory = new InMemoryNodeDirectory();

    public synchronized void update(InMemoryNodeDirectory newDirectory) {
        this.nodeDirectory = newDirectory;
    }

    @Override
    public List<TargetNode> getAll() {
        return nodeDirectory.getAll();
    }

    @Override
    public void insert(TargetNode node) {
        nodeDirectory.insert(node);
    }
}

