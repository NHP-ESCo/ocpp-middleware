package it.besmart.ocpp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileTransferUtility {

	
	private String FTP_USER;
	
	private String FTP_PWD;
	
	private String FTP_SERVER;
	
	private String SERVER_DIR;
	
	private String BASE_DIR;
	

	private final Logger logger = LoggerFactory.getLogger(FileTransferUtility.class);
	
	
	public void configureFtpServer(String user, String password, String serverUrl, 
			String serverDirectory, String baseDirectory) {
		
		this.FTP_USER = user;
		this.FTP_PWD = password;
		this.FTP_SERVER = serverUrl;
		this.SERVER_DIR = serverDirectory;
		this.BASE_DIR = baseDirectory;
		
		logger.debug(String.format("Ftp created %s", this.toString()));
	}

	
	public String getServerPath(boolean sftp) {
		String uri = FTP_USER + ":" + FTP_PWD + "@" + FTP_SERVER;
		
		if(sftp)
			uri = "sftp://"+ uri + ":22";
		else
			uri = "ftp://" + uri + ":21";
		
		return uri + SERVER_DIR;
	}
	
	public String getServerFile(String dir, boolean sftp) throws IOException {
		String folder = getServerPath(sftp) + dir;
		
		if(!folder.substring(folder.length()-1, folder.length()).contains("/"))
			folder += "/";
		
		FTPClient client = null;
		
		try{
			client = getFTPConnection();
		}
		catch(IOException e) {
			
			throw new IOException("Client connection failed");
		}
		
		FTPFile[] files = null;
		
		try {
			files = client.listFiles(folder);
		}
		catch(IOException e) {
			
			throw new IOException(String.format("Folder %s not found on server", folder));
		}
		
		//TODO does not list files in folder
		if(files.length==1) {
			return folder + files[0].getName();
		}
		else {
			
			throw new IOException(String.format("%d files in folder %s : %s", files.length, folder, files.toString()));
		}
	}
	
	
	
	public File downloadFile(String path, boolean sftp) throws IOException {
		
		
		if(!sftp) {
			
			String remotePath = SERVER_DIR + path;
			String localPath = BASE_DIR + "/" + getFileName(path);
			
			FileOutputStream out = new FileOutputStream(localPath);
			
			FTPClient client = getFTPConnection();
			
			
            boolean result = client.retrieveFile(remotePath, out);
            out.close();
            
            if(!result)
            	throw new IOException("Failed Download");
            else
            	client.deleteFile(remotePath);
            
            File file = new File(localPath);
            return file;
            
		}
		else {
			
			//TODO manage sftp
			throw new IOException();
			
		}
		
		

	}


	private String getFileName(String path) {
		String[] comps = path.split("/");
		
		return comps[comps.length-1];
	}


	private FTPClient getFTPConnection() throws IOException {
		FTPClient client = new FTPClient();
		
		client.connect(FTP_SERVER, 21);
		client.login(FTP_USER, FTP_PWD);
		client.enterLocalPassiveMode();
		
		return client;
	}

	@Override
	public String toString() {
		return "FileTransferUtility [FTP_USER=" + FTP_USER + ", FTP_PWD=" + FTP_PWD + ", FTP_SERVER=" + FTP_SERVER
				+ ", SERVER_DIR=" + SERVER_DIR + ", BASE_DIR=" + BASE_DIR + "]";
	}
	
	
}
