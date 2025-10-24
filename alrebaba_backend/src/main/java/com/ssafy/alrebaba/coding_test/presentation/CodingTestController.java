package com.ssafy.alrebaba.coding_test.presentation;

import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ssafy.alrebaba.coding_test.application.CodingTestService;
import com.ssafy.alrebaba.coding_test.dto.request.CodingTestCreateRequest;
import com.ssafy.alrebaba.coding_test.dto.request.CodingTestFilterRequest;
import com.ssafy.alrebaba.coding_test.dto.response.CodingTestResponse;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;
import com.ssafy.alrebaba.problem.application.ProblemService;
import com.ssafy.alrebaba.problem.dto.request.ProblemUpdateRequest;
import com.ssafy.alrebaba.problem.dto.response.ProblemResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/coding-tests")
@RequiredArgsConstructor
public class CodingTestController {

    private final CodingTestService codingTestService;
    private final ProblemService problemService;

    /**
     * 코딩테스트 관련 엔드포인트
     */
    //코딩테스트 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCodingTest(@Valid @RequestBody CodingTestCreateRequest codingTestCreateRequest,
                                                                @AuthenticationPrincipal CustomMemberDetails loginMember) throws BadRequestException, IllegalAccessException {
        Long codingTestId = codingTestService.create(codingTestCreateRequest, loginMember);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(codingTestId)
                .toUri();

        Map<String, Object> responseBody = Map.of(
                "codingTestId", codingTestId
        );

        return ResponseEntity.created(location).body(responseBody);
    }


    @GetMapping
    public ResponseEntity<?> getCodingTest(@ModelAttribute CodingTestFilterRequest codingTestFillterRequst) throws
        BadRequestException {

        boolean allNull = (codingTestFillterRequst.codingTestId() == null
            && codingTestFillterRequst.channelId() == null
            && codingTestFillterRequst.startTime() == null
            && codingTestFillterRequst.endTime() == null);

        if (allNull) {
            throw new BadRequestException("최소 한 가지 필터값은 필수입니다.");
        }

        List<CodingTestResponse> codingTestResponseList = codingTestService.get(codingTestFillterRequst);
       return ResponseEntity.ok(codingTestResponseList);
    }

    @DeleteMapping("/{coding-test-id}")
    public ResponseEntity<Void> deleteCodingTest(@PathVariable(name = "coding-test-id") Long codingTestId,
                                                 @AuthenticationPrincipal CustomMemberDetails loginMember) throws IllegalAccessException {
        codingTestService.delete(codingTestId, loginMember);
        return ResponseEntity.noContent().build();
    }

    /**
     * 코딩테스트 문제 관련 엔드포인트
     */
    //코딩테스트 문제 생성
//    @PostMapping("/problems")
//    public ResponseEntity<Map<String,Object>> createProblem(@RequestBody ProblemCreateRequest problemCreateRequest,
//                                          @AuthenticationPrincipal CustomMemberDetails loginMember) throws IllegalAccessException {
//        var location = ServletUriComponentsBuilder
//            .fromCurrentRequest()
//            .path("/{id}")
//            .buildAndExpand(problemCreateRequest.codingTestId())
//            .toUri();
//
//        List<Long> prolbemIdList = problemService.create(problemCreateRequest, loginMember);
//        Map<String, Object> responseBody = Map.of(
//                "problemIds", prolbemIdList
//        );
//        return ResponseEntity.created(location).body(responseBody);
//    }

    @GetMapping("/{coding-test-id}/problems")
    public ResponseEntity<List<ProblemResponse>> getProblem(@PathVariable(name = "coding-test-id") Long codingTestId,
                                                            @RequestParam(name = "problemId", required = false) Long problemId){

        List<ProblemResponse> problemResponseList = problemService.get(codingTestId, problemId);
        return ResponseEntity.ok(problemResponseList);
    }

    @PutMapping("/{coding-test-id}/problems/{problem-id}")
    public ResponseEntity<Void> updateProblem(@PathVariable(name = "coding-test-id") Long codingTestId,
                                          @PathVariable(name = "problem-id") Long problemId,
                                          @Valid @RequestBody ProblemUpdateRequest problemUpdateRequest,
                                          @AuthenticationPrincipal CustomMemberDetails loginMember) throws IllegalAccessException {
        problemService.update(codingTestId, problemId, problemUpdateRequest, loginMember);
        return ResponseEntity.ok().build();
    }

}
