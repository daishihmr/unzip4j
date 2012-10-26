package jp.dev7.unzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFileEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class Unzip {
	private static Logger log = LoggerFactory.getLogger(Unzip.class);

	public static void main(String[] args) throws Exception {
		final String dest = "target/dest-" + System.currentTimeMillis();
		new Unzip().unzip(dest, "boss3.zip", new File("boss3.zip"), "suf",
				"atr", "l3p", "l3c", "bmp");
	}

	public void unzip(String outDir, String zipFileName) throws Exception {
		unzip(outDir, zipFileName, new File(zipFileName));
	}

	@SuppressWarnings("unchecked")
	public void unzip(String outDir, String zipFileName, File zipFile,
			String... extWhiteList) throws Exception {
		log.info("ファイル " + zipFile.getName() + "を解凍");

		Arrays.sort(extWhiteList);
		for (int i = 0; i < extWhiteList.length; i++) {
			if (!extWhiteList[i].startsWith(".")) {
				extWhiteList[i] = "." + extWhiteList[i];
			}
		}

		final String destDir = outDir + "/"
				+ zipFileName.toLowerCase().replace(".zip", "");
		final ZipFileEx zipFileEx = new ZipFileEx(zipFile);
		final Enumeration<ZipEntry> entries = zipFileEx.getEntries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();

			if (entry.isDirectory()) {
				continue;
			}

			String name = entry.getName();
			final String f = zipFileName.toLowerCase().replace(".zip", "");
			if (name.startsWith(f + "/")) {
				name = name.replace(f + "/", "");
			}

			final File dest = new File(destDir, name.toLowerCase());
			final String ext = dest.getName().substring(
					dest.getName().lastIndexOf("."));
			if (Arrays.binarySearch(extWhiteList, ext) < 0) {
				log.info(dest.getName() + "(拡張子=[" + ext
						+ "])はホワイトリストに入ってないのでスキップ");
				continue;
			}
			Files.createParentDirs(dest);

			Files.copy(new InputSupplier<InputStream>() {
				public InputStream getInput() throws IOException {
					return zipFileEx.getInputStream(entry);
				}
			}, dest);

			log.info("ファイル作成: " + dest.getAbsolutePath());
		}
	}

}
