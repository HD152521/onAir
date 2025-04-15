package com.sejong.project.onair.domain.member;

import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member")
    private List<UploadFile> uploadFiles;

    @NotNull
    private String memberName;     //실명
    @NotNull private String email;          //이메일
    @NotNull private Role role;             //권한
    @NotNull private boolean isFirstLogin;  //처음로그인


    public enum Role{
        USER("USER"),
        ADMIN("ADMIN");

        Role(String role){}
        private String role;
    }
}
