package com.orange.atk.sign.apk;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.log4j.Logger;

import com.android.uiautomator.robotiumTask.PrepareApkForRobotiumTest;
import com.orange.atk.phone.PhoneException;
import com.orange.atk.platform.Platform;

public class SignAPK {

	@SuppressWarnings("deprecation")
	public static void signApk(String input, String output) throws PhoneException{
		String keystoreFilename = Platform.getInstance().getAtkKeyLocation();
		char[] password = "ATKKEY".toCharArray();
		String alias = "ATKKEY";
		SignedJarBuilder mBuilder = null;
		try {
			FileInputStream fIn = new FileInputStream(keystoreFilename);
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(fIn, password);
			KeyStore.PrivateKeyEntry key = (KeyStore.PrivateKeyEntry)keystore.getEntry(alias, new KeyStore.PasswordProtection(password));
			mBuilder = new SignedJarBuilder(new FileOutputStream(output, false), key.getPrivateKey(), (X509Certificate)key.getCertificate());
			mBuilder.writeZip(new FileInputStream(input), new NullZipFilter());
			mBuilder.close();
		} catch (FileNotFoundException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("FileNotFoundException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (KeyStoreException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("KeyStoreException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("NoSuchAlgorithmException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (CertificateException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("CertificateException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (IOException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("IOException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (UnrecoverableEntryException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("UnrecoverableEntryException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (SignedJarBuilder.IZipEntryFilter.ZipAbortException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("NoSuchAlgorithmException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (GeneralSecurityException e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("NoSuchAlgorithmException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(PrepareApkForRobotiumTest.class ).debug("NoSuchAlgorithmException error While signing apk  " + e.getMessage());
			throw new PhoneException(e.getMessage());
		} finally {
			if (mBuilder != null)
				mBuilder.cleanUp();
		}
	}
	public static void  zipAlignApk(String input, String output) throws PhoneException {
		Runtime r = Runtime.getRuntime();
		String zipalignLocation=Platform.getInstance().getZipalignLocation();
		String zipalign[] = {zipalignLocation, "4",input, output};
		try {
			Process p = r.exec(zipalign, null, new File(
					Platform.getInstance().getJATKPath()+Platform.FILE_SEPARATOR+"AndroidTools"+
					Platform.FILE_SEPARATOR + "UiautomatorViewerTask"));
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader errorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line = "";
			while ((line = inputStream.readLine()) != null) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"Zipalign apk : " + line);
			}
			inputStream.close();
			while ((line = errorStream.readLine()) != null) {
				Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
						"Zipalign apk : " + line);
			}
			errorStream.close();
		} catch (IOException e1) {
			Logger.getLogger(PrepareApkForRobotiumTest.class).debug(
					"/****error : " + e1.getMessage());
			throw new PhoneException(e1.getMessage());

		}
	}
	@SuppressWarnings("deprecation")
	private static final class NullZipFilter implements SignedJarBuilder.IZipEntryFilter
	{
		public boolean checkEntry(String archivePath) throws SignedJarBuilder.IZipEntryFilter.ZipAbortException
		{
			return true;
		}
	}
}
