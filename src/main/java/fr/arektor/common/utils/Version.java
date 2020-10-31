package fr.arektor.common.utils;

import org.bukkit.Bukkit;

public enum Version {

	UNSUPPORTED("v1_0_R1"),
	V1_16_R2("v1_16_R2"),
	V1_16_R1("v1_16_R1"),
	V1_15_R1("v1_15_R1"),
	V1_14_R1("v1_14_R1"),
	V1_13_R2("v1_13_R2"),
	V1_13_R1("v1_13_R1"),
	V1_12_R1("v1_12_R1"),
	V1_11_R1("v1_11_R1");
	
	protected static Version version;
	protected static String detected;
	
	private String identifier;
	private Version(String identifier) { this.identifier = identifier; }
	public String getIdentifier() { return this.identifier; }
	
	public static Version getVersion() {
		return version;
	}
	
	public static void checkVersion() {
		String nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
		detected = nmsver;
		for (Version v : Version.values()) {
			if (v.getIdentifier().equalsIgnoreCase(nmsver)) {
				version = v;
				break;
			}
		}
	}
	
	public static String getDetectedVersion() {
		return detected;
	}
}
