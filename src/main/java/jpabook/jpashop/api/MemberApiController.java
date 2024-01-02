package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Reslut membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Reslut(collect.size(),collect);
    }


    @Data

    @AllArgsConstructor
    static class Reslut<T>{
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }




    /*
    @Valid 검증한다.
    * @Valid 을 하고 Entity에서 Notnull등 여러거지 어노테이션을 사용하면 값이 들어가기전에 검증을 할 수 있다.
    * -> null값은 못들어오게 막을 수 있다.
    * Entity를 파라미터로 받는 자체가 좋지않다.
    * */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }


    /*
    * DTO를 만들어야 하는 이유
    * api 스펙이나 누군가가 객체의 변수명을 변경했을때 오류가 발생함
    * -> 변견된 부분만 찾아서 변경해주면 된다 . -> 안전성
    * */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request ){
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id ,
                                               @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id , request.getName());
        Member findMember = memberService.findOne(id);
        /*
        * 커맨드와 쿼리를 분리시켜 유지보수를 증가시킴 트래픽을 많이 발생시키지않는다면 이 방법도 좋음
        * */
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());

    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }


    @Data
    static class CreateMemberRequest{
       private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }

    }







//    private final MemberService memberService;
//
//    //    @RequestBody -> Api을 통해 Json으로 들어온 값을 전부 변환해서 Member안에 삽입시켜줌
//    //Request를 따로 만들지 않으면 Entity값이 변경 되었을때 api스펙이 변경된다
//    //왜냐하면 Member 중 하나의 값을 받는거니깐
//    @PostMapping("/api/v1/members")
//    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
//        Long id = memberService.join(member);
//
//        return new CreateMemberResponse(id);
//    }
//
//    //Request를 따로 만들어서 Entity값이 변경 되었을때 controller의 로직만 바꿔준다.
//    @PostMapping("/api/v2/members")
//    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
//
//        Member member = new Member();
//        member.setName(request.getName());
//
//        Long id = memberService.join(member);
//        return new CreateMemberResponse(id);
//    }
//
//    @PutMapping("/api/v2/members/{id}")
//    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
//                                               @RequestBody @Valid UpdateMemberRequest request){
//
//        memberService.update(id, request.getName());
//        Member findMember = memberService.findOne(id);
//        return new UpdateMemberResponse(findMember.getId() , findMember.getName());
//    }
//
//    //-------------------------------
//
//    //List<Member> 엔티티를 넣게 되면 반환되는 값이 json array값이 반환되기에
//    //확장성이 급격하게 떨어진다.
//    @GetMapping("/api/v1/members")
//    public List<Member> membersV1(){
//        return memberService.findMembers();
//    }
//
//    @GetMapping("/api/v2/members")
//    public Result memberV2(){
//        List<Member> findMembers = memberService.findMembers();
//        List<MemberDto> collect = findMembers.stream()
//                .map(m -> new MemberDto(m.getName()))
//                .collect(Collectors.toList());
//        return new Result(collect.size(),collect);
//    }
//    @Data
//    @AllArgsConstructor
//    static class Result<T>{
//        private int count;
//        private T data;
//    }
//    @Data
//    @AllArgsConstructor
//    static class MemberDto{
//        private String name;
//    }
//
//
//
//    @Data
//    static class CreateMemberRequest {
//        private String name;
//    }
//
//    @Data
//    static class CreateMemberResponse {
//        private Long id;
//
//        public CreateMemberResponse(Long id) {
//            this.id = id;
//        }
//    }
//
//    @AllArgsConstructor
//    @Data
//    static class UpdateMemberResponse{
//        private Long id;
//        private String name;
//
//
//    }
//    @Data
//    static class UpdateMemberRequest{
//        private String name;
//    }

}
