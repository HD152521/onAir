package com.sejong.project.onair.domain.file.model;

import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.nio.file.Path;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE upload_file SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
@Table(
        name = "upload_file",
        indexes = {
                @Index(name = "idx_file_id", columnList = "file_id")
        }
)
public class UploadFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uploadFileName;
    private String storeFileName;
    private String filePath;
    private double fileSize;
    @Column(unique = true) private String fileId;

    @Transient
    private Path realPath;

    private FileType fileType;

    @OneToMany(mappedBy = "uploadFile")
    private List<FileData> fileData;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder
    public UploadFile(String uploadFileName, String storeFileName, String filePath,Path realPath,String fileId,Member member, FileType fileType,
                      double fileSize
    ) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.filePath = filePath;
        this.realPath = realPath;
        this.fileId = fileId;
        this.member = member;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

}
