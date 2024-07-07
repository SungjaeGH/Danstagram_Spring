package com.project.danstagram.domain.search.entity;

import jakarta.persistence.GeneratedValue;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "member")
@Setting(settingPath = "elastic/es-setting.json")
@Mapping(mappingPath = "elastic/member/es-mapping.json")
@Getter
public class MemberDocument {

    @Id
    @GeneratedValue
    private final String memberIdx;

    @Field(name = "memberId", type = FieldType.Text)
    private final String memberId;

    @Field(name = "memberName", type = FieldType.Text)
    private final String memberName;

    @Field(name = "memberStoreImage")
    private String memberStoreImage;

    @Builder
    public MemberDocument(String memberIdx, String memberId, String memberName, String memberStoreImage) {
        this.memberIdx = memberIdx;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberStoreImage = memberStoreImage;
    }

    public void setMemberStoreImage(String memberStoreImage) {
        this.memberStoreImage = memberStoreImage;
    }
}
