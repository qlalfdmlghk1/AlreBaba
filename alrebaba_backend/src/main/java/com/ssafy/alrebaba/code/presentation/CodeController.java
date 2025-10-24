package com.ssafy.alrebaba.code.presentation;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ssafy.alrebaba.code.application.CodeService;
import com.ssafy.alrebaba.code.dto.request.CRDTMessage;
import com.ssafy.alrebaba.code.dto.request.CodeCreateRequest;
import com.ssafy.alrebaba.code.dto.request.CodeFilterRequest;
import com.ssafy.alrebaba.code.dto.request.CodePatchRequest;
import com.ssafy.alrebaba.code.dto.response.CodePageResponse;
import com.ssafy.alrebaba.member.dto.request.CustomMemberDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
public class CodeController {

	private final CodeService codeService;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 코드 관련 엔드포인트
	 */

	@MessageMapping("/crdt")
	public void sendMessage(@Payload CRDTMessage crdtMessage) {
		System.out.println("받은 메시지: " + crdtMessage);
		codeService.sendCRDT(crdtMessage);
	}

	@MessageMapping("/snapshot")
	public void sendSnapShot(@Payload CRDTMessage crdtMessage){codeService.sendSnapShot(crdtMessage);}


	// 코드 생성
	@PostMapping("/{channel-id}")
	public ResponseEntity<Map<String, Object>> createCode(@PathVariable(name = "channel-id") Long channelId,
														  @Valid @RequestBody CodeCreateRequest codeCreateRequest,
														  @AuthenticationPrincipal CustomMemberDetails loginMember) throws BadRequestException {

		Long codeId = codeService.create(channelId, codeCreateRequest,loginMember);
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
			.path("/code/{id}").buildAndExpand(codeId).toUri();

		Map<String, Object> responseBody = Map.of(
				"codeId", codeId
		);

		return ResponseEntity.created(uri).body(responseBody);

	}

	// 코드 검색
	@GetMapping
	public ResponseEntity<CodePageResponse> getCode(@ModelAttribute CodeFilterRequest codeFilterRequest) throws BadRequestException {

		boolean allNull = (codeFilterRequest.codeId() == null
				&& codeFilterRequest.channelId() == null
				&& codeFilterRequest.platform() == null
				&& codeFilterRequest.title() == null
				&& codeFilterRequest.language() == null
				&& codeFilterRequest.memberId() == null
				&& codeFilterRequest.problemId() == null);

		if (allNull) {
			throw new BadRequestException("최소 한 가지 필터값은 필수입니다.");
		}

		CodePageResponse codePageResponse= codeService.get(codeFilterRequest);
		return ResponseEntity.ok(codePageResponse);
	}

	@GetMapping("/details/{code-id}")
	public ResponseEntity<Map<String, String>> getCodeDetail(@PathVariable("code-id")Long codeId){
		String context = codeService.getDetail(codeId);
		Map<String, String> response = Map.of(
				"context", context
		);

		return ResponseEntity.ok(response);
	}

	@PatchMapping
	public ResponseEntity<Void> patchCode(@Valid @RequestBody CodePatchRequest codePatchRequest,
										  @AuthenticationPrincipal CustomMemberDetails loginMember) throws AccessDeniedException {
		codeService.patch(codePatchRequest, loginMember);

		return ResponseEntity.ok().build();
	}
}
