package com.ssafy.alrebaba.study.presentation;

import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.study.application.StudyService;
import com.ssafy.alrebaba.study.dto.request.StudyCreateRequest;
import com.ssafy.alrebaba.study.dto.request.StudyUpdateRequest;
import com.ssafy.alrebaba.study.dto.response.StudyListResponse;
import com.ssafy.alrebaba.study.dto.response.StudyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
@Tag(name = "Study", description = "스터디 관리 API")
public class StudyController {

    private final StudyService studyService;

    /**
     * 스터디 관련 엔드포인트
     */
    // 스터디 생성
    @PostMapping
    @Operation(
            summary = "스터디 생성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "스터디 생성 성공",
                            content = @Content(schema = @Schema(implementation = Map.class)))
            }
    )
    public ResponseEntity<Map<String, Object>> createStudy(
            @Valid @RequestBody StudyCreateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember) {
        Long loginId = loginMember.getMemberId();
        Long studyId = studyService.create(loginId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/studies/{id}").buildAndExpand(studyId).toUri();
        Map<String, Object> response = Map.of("studyId", studyId);
        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping(value = "/{study-id}",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "스터디 이미지 업로드", description = "특정 스터디의 프로필 이미지를 업로드합니다.",
            responses = @ApiResponse(responseCode = "201", description = "스터디 이미지 업로드 성공"))
    public ResponseEntity<?> uploadStudyImage(@PathVariable("study-id") Long studyId,
                                              @Parameter(
                                                      description = "multipart/form-data 형식의 이미지를 input으로 받습니다.",
                                                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
                                              )
                                              @RequestPart MultipartFile multipartFile,
                                              @AuthenticationPrincipal CustomMemberDetails loginMember) throws IOException {
       String url =  studyService.uploadStudyImage(studyId, multipartFile, loginMember);
        return ResponseEntity.created(URI.create(url)).build();
    }

    // 내가 참여한 스터디 목록
    @GetMapping
    @Operation(summary = "회원이 참여한 스터디 목록 조회 ", description = "회원이 참여한 스터디 목록을 조회합니다.",
            responses = @ApiResponse(responseCode = "200", description = "참여한 스터디 목록 조회 성공"))
    public ResponseEntity<?> getStudy(@AuthenticationPrincipal CustomMemberDetails loginMember){
        List<StudyListResponse> studyResponseList = studyService.getList(loginMember);
        return ResponseEntity.ok(studyResponseList);
    }

    // 스터디 상세 조회
    @GetMapping("/{study-id}")
    @Operation(summary = "스터디 상세 조회", description = "특정 스터디의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "스터디 상세 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = StudyResponse.class)))
            })
    public ResponseEntity<StudyResponse> getStudy(@PathVariable("study-id") Long studyId) {
        StudyResponse response = studyService.get(studyId);
        return ResponseEntity.ok(response);
    }

    // 스터디 수정
    @PatchMapping("/{study-id}")
    @Operation(summary = "스터디 수정", description = "특정 스터디의 정보를 수정합니다.",
            responses = @ApiResponse(responseCode = "204", description = "스터디 수정 성공"))
    public ResponseEntity<Void> updateStudy(
            @PathVariable("study-id") Long studyId,
            @Valid @RequestBody StudyUpdateRequest request,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {
        Long loginId = loginMember.getMemberId();
        studyService.update(studyId, loginId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{study-id}")
    public ResponseEntity<Void> deleteStudy(
            @PathVariable("study-id") Long studyId,
            @AuthenticationPrincipal CustomMemberDetails loginMember){

        studyService.delete(studyId, loginMember);
        return ResponseEntity.noContent().build();
    }




}
