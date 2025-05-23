package com.sejong.project.onair.domain.file.model;

public enum FileType {
    CSV("csv", "text/csv"),
    XLS("xls", "application/vnd.ms-excel"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String extension;
    private final String mimeType;

    FileType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static FileType fromExtension(String ext) {
        for (FileType type : values()) {
            if (type.extension.equalsIgnoreCase(ext)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + ext);
    }

    public static FileType fromMimeType(String mime) {
        for (FileType type : values()) {
            if (type.mimeType.equalsIgnoreCase(mime)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + mime);
    }
}
