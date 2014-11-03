package org.jenkinsci.plugins.uniqueid.impl;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.hudson.plugins.folder.FolderProperty;
import com.cloudbees.hudson.plugins.folder.FolderPropertyDescriptor;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Actionable;
import org.jenkinsci.plugins.uniqueid.IdStore;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores ids for folders as a {@link FolderIdProperty}
 */
@Extension(optional = true)
public class FolderIdStore extends IdStore<Folder> {
    public FolderIdStore() {
        super(Folder.class);
    }

    @Override
    public void make(Folder folder) {
        if (folder.getProperties().get(FolderIdProperty.class) == null) {
            try {
                folder.addProperty(new FolderIdProperty());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to add property",e);
            }
        }
    }

    @Override
    public String get(Folder folder) {
        String id = Id.getId((Actionable) folder);
        if (id != null) {
            return id;
        } else {
            FolderIdProperty idProperty = folder.getProperties().get(FolderIdProperty.class);
            if (idProperty != null) {
                return idProperty.getId();
            }
        }
        return null;
    }

    /**
     * A unique ID for folders.
     */
    public static class FolderIdProperty extends FolderProperty {
        private Id id = new Id();

        @Override
        public Collection<? extends Action> getFolderActions() {
            return Collections.singleton(id);
        }

        @Extension(optional = true)
        public static class DescriptorImpl extends FolderPropertyDescriptor {
            @Override
            public String getDisplayName() {
                return "Unique ID";
            }

            @Override
            public boolean isApplicable(Class<? extends Folder> containerType) {
                return false;
            }
        }

        /**
         * Since {@link Folder#getAction(Class)} does return actions from a property,
         * (like {@link hudson.model.Job#getAction(Class)} does)
         * this method is added for convenience.
         *
         * @return the id for this folder
         */
        public String getId() {
            return id.getId();
        }
    }

    private final static Logger LOGGER = Logger.getLogger(FolderIdStore.class.getName());

}
