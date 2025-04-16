package com.yana.privateNetTest.LocalMachine.exchange.recv;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;


public class ExchangeDirMonitor extends Thread {
	private static final Path path = Paths.get("exchanger");
	private WatchService watchService;
	private boolean monitorContinue;
	private ExchangeDirMonitor() throws Exception {
		this.watchService = FileSystems.getDefault().newWatchService();
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		monitorContinue = true;
	}

	public static void executeDirMonitor() throws Exception {
		File[] initFileList = path.toFile().listFiles();
		for(File f : initFileList) {
			if(f.isDirectory()) {
				continue;
			}
			ExchangeDirCollectInfo.addFile(f.toPath());
		}
		new ExchangeDirMonitor().start();
	}

	@Override
	public void run() {
		while(monitorContinue) {
			WatchKey key = takeKeyWatch();
			if(key == null) {
				continue;
			}
			for(WatchEvent<?> e : key.pollEvents()) {
				Kind<?> kind = e.kind();
				Object obj = e.context();
				if(obj instanceof Path) {
					Path p = (Path)obj;
					if(p.toFile().isDirectory()) {
						continue;
					}
					if(kind == StandardWatchEventKinds.ENTRY_CREATE) {
						//ExchangeDirCollectInfo ADD
						String fileName = path.toFile().getName() + File.separator + p.toFile().getName();
						ExchangeDirCollectInfo.addFile(Paths.get(fileName));
					}
					if(kind == StandardWatchEventKinds.ENTRY_DELETE) {
						String fileName = path.toFile().getName() + File.separator + p.toFile().getName();
						ExchangeDirCollectInfo.deleteFile(Paths.get(fileName));
					}
				}
			}
			key.reset();
		}
	}
	
	private WatchKey takeKeyWatch() {
		try {
			return watchService.take();
		} catch (InterruptedException e) {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
		while(true) {
			WatchKey key;
			key = watchService.take();
			for(WatchEvent<?> e : key.pollEvents()) {
				Kind<?> k = e.kind();
				Object obj = e.context();
				if(obj instanceof Path) {
					System.out.println("OK");
					Path p = (Path)obj;
					System.out.println(p.toFile().getName());
					if(k == StandardWatchEventKinds.ENTRY_DELETE) {
						System.out.println("ENTRY_DELETE");
					}
					if(k == StandardWatchEventKinds.ENTRY_CREATE) {
						System.out.println("ENTRY_CREATE");
					}
					if(k == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("ENTRY_MODIFY");
					}
				}
			}
			key.reset();
		}
	}
}
