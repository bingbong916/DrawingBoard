package kr.ac.konkuk.ccslab.cm.thread;

import kr.ac.konkuk.ccslab.cm.entity.CMFileSyncEntry;
import kr.ac.konkuk.ccslab.cm.event.filesync.CMFileSyncEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMFileSyncManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CMFileSyncGenerator implements Runnable {
    private String userName;
    private CMInfo cmInfo;
    private List<CMFileSyncEntry> fileEntryList;
    private List<Path> basisFileList;
    private List<Path> newFileList;

    public CMFileSyncGenerator(String userName, CMInfo cmInfo) {
        this.userName = userName;
        this.cmInfo = cmInfo;
        fileEntryList = null;
        basisFileList = null;
        newFileList = null;
    }

    @Override
    public void run() {
        if(CMInfo._CM_DEBUG) {
            System.out.println("CMFileSyncGenerator.run() called..");
        }

        // get client file-entry-list
        fileEntryList = cmInfo.getFileSyncInfo().getFileEntryListHashtable().get(userName);
        if(fileEntryList == null) {
            System.err.println("CMFileSyncGenerator.run(), fileEntryList is null!");
            return;
        }
        if(CMInfo._CM_DEBUG) {
            System.out.println("fileEntryList = " + fileEntryList);
        }

        // create a basis file-entry-list at the server
        basisFileList = createBasisFileList();
        if(basisFileList == null) {
            System.err.println("CMFileSyncGenerator.run(), basisFileList is null!");
            return;
        }
        if(CMInfo._CM_DEBUG) {
            System.out.println("basisFileList = " + basisFileList);
        }

        //// compare the client file-entry-list and the basis file-entry-list

        // delete files that exists only at the server and update the basisFileList
        deleteFilesAndUpdateBasisFileList();
        if(CMInfo._CM_DEBUG) {
            System.out.println("basisFileList after the deletion = " + basisFileList);
        }

        // create a new file-entry-list that will be added to the server
        newFileList = createNewFileList();
        if(newFileList == null) {
            System.err.println("CMFileSyncGenerator.run(), newFileList is null!");
            return;
        }
        if(CMInfo._CM_DEBUG) {
            System.out.println("newFileList = " + newFileList);
        }

        // request the files in the new file-entry-list from the client
        boolean requestResult = requestTransferOfNewFiles();
        // from here

        // update the files at the server by synchronizing with those at the client

    }

    private boolean requestTransferOfNewFiles() {
        if(CMInfo._CM_DEBUG) {
            System.out.println("CMFileSyncGenerator.requestTransferOfNewFiles() called..");
        }

        if(newFileList == null) {
            System.err.println("newFileList is null!");
            return false;   // null is an error
        }
        if(newFileList.isEmpty()) {
            System.out.println("newFileList is empty.");
            return true;
        }

        int numRequestsCompleted;
        for(numRequestsCompleted = 0; numRequestsCompleted < newFileList.size(); numRequestsCompleted++) {

            // create a request event
            CMFileSyncEvent fse = new CMFileSyncEvent();
            fse.setID(CMFileSyncEvent.REQUEST_NEW_FILES);
            // from here

        }

        return true;
    }

    private List<Path> createNewFileList() {
        if(CMInfo._CM_DEBUG) {
            System.out.println("CMFileSyncGenerator.createNewFileList() called..");
        }
        // get the start path index
        CMFileSyncManager syncManager = (CMFileSyncManager) cmInfo.getServiceManagerHashtable()
                .get(CMInfo.CM_FILE_SYNC_MANAGER);
        Path serverSyncHome = syncManager.getServerSyncHome(userName);
        int startPathIndex = serverSyncHome.getNameCount();
        // get the relative path list from the basis file list
        List<Path> relativeBasisFileList = basisFileList.stream()
                .map(path -> path.subpath(startPathIndex, path.getNameCount()))
                .collect(Collectors.toList());
        // create a new file list that will be added to the server
        List<Path> newFileList = fileEntryList.stream()
                .map(CMFileSyncEntry::getPathRelativeToHome)
                .filter(path -> !relativeBasisFileList.contains(path))
                .collect(Collectors.toList());

        return newFileList;
    }

    private void deleteFilesAndUpdateBasisFileList() {
        if(CMInfo._CM_DEBUG) {
            System.out.println("CMFileSyncGenerator.deleteFilesOnlyAtServer() called..");
        }
        // get the client file-entry-list
        List<CMFileSyncEntry> fileEntryList = cmInfo.getFileSyncInfo().getFileEntryListHashtable().get(userName);
        if(fileEntryList == null) {
            System.err.println("CMFileSyncGenerator.createDeletedFileList(), fileEntryList of user("+userName
                    +") is null!");
            return;
        }
        // get the client path list from the file-entry-list
        List<Path> entryPathList = fileEntryList.stream()
                .map(CMFileSyncEntry::getPathRelativeToHome)
                .collect(Collectors.toList());
        // get the CMFileSyncManager object
        CMFileSyncManager syncManager = (CMFileSyncManager) cmInfo.getServiceManagerHashtable()
                .get(CMInfo.CM_FILE_SYNC_MANAGER);
        //// create target file list that exists only at the server and that will be deleted
        // get the server sync home and the start index
        Path serverSyncHome = syncManager.getServerSyncHome(userName);
        int startPathIndex = serverSyncHome.getNameCount();
        // create the deleted file list
        basisFileList.stream()
                .filter(path -> !entryPathList.contains(path.subpath(startPathIndex, path.getNameCount())))
                .forEach(path -> {
                    try {
                        if(CMInfo._CM_DEBUG)
                            System.out.println("path = " + path);
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        // update the basis file list
        basisFileList = basisFileList.stream()
                .filter(path -> entryPathList.contains(path.subpath(startPathIndex, path.getNameCount())))
                .collect(Collectors.toList());

    }

    private List<Path> createBasisFileList() {
        if(CMInfo._CM_DEBUG) {
            System.out.println("CMFileSyncGenerator.createBasisFileList() called..");
        }
        // get the file sync manager
        CMFileSyncManager syncManager = (CMFileSyncManager) cmInfo.getServiceManagerHashtable()
                .get(CMInfo.CM_FILE_SYNC_MANAGER);
        if(syncManager == null) {
            System.err.println("CMFileSyncGenerator.createBasisFileList(), file-sync manager is null!");
            return null;
        }
        // get the server sync home
        Path serverSyncHome = syncManager.getServerSyncHome(userName);
        // check if the sync home exists or not
        if(Files.notExists(serverSyncHome)) {
            System.err.println("CMFileSyncGenerator.createBasisFileList(), the server sync-home does not exist!");
            return null;
        }
        // create a basis file list
        return syncManager.createPathList(serverSyncHome);
    }
}