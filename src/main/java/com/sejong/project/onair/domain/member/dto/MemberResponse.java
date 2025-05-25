package com.sejong.project.onair.domain.member.dto;

import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.global.token.vo.AccessToken;

import java.util.List;

public class MemberResponse {
    public record LoginResponseDto(
            String memberName,
            String email,
            boolean isFirstLogin
    ){
        public static LoginResponseDto from(Member member){
            return new LoginResponseDto(
                    member.getMemberName(),
                    member.getEmail(),
                    member.isFirstLogin()
            );
        }
    }

    public record MemberProfileDto(
            String name,
            String email,
            String imgUrl,
            List<FileResponse.FileLogDto> fileLogs
            //todo 파일 업로드 이력(파일 이름, 업로드 날짜,크기)
    ){
        public static MemberProfileDto from(Member member, List<FileResponse.FileLogDto> logs){
            return new MemberProfileDto(
                    member.getMemberName(),
                    member.getEmail(),
                    member.getImgUrl(),
                    logs
            );
        }
    }
}
