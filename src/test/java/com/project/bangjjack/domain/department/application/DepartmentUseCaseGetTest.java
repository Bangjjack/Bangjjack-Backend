package com.project.bangjjack.domain.department.application;

import com.project.bangjjack.domain.department.application.dto.response.DepartmentResponse;
import com.project.bangjjack.domain.department.application.exception.InvalidCampusException;
import com.project.bangjjack.domain.department.application.usecase.DepartmentUseCase;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DepartmentUseCaseGetTest {

    @Mock
    private DepartmentGetService departmentGetService;

    @InjectMocks
    private DepartmentUseCase departmentUseCase;

    @Nested
    @DisplayName("캠퍼스별 학과 목록 조회 시")
    class GetDepartmentsByCampus {

        @Test
        @DisplayName("GLOBAL_CAMPUS 조회 시 글로벌 캠퍼스 학과 목록만 반환된다")
        void GLOBAL_CAMPUS_조회_시_글로벌_캠퍼스_학과_목록_반환() {
            // given
            List<Department> departments = List.of(
                    Department.create("소프트웨어학과", Campus.GLOBAL_CAMPUS),
                    Department.create("경영학과", Campus.GLOBAL_CAMPUS)
            );
            given(departmentGetService.findAllByCampus(Campus.GLOBAL_CAMPUS)).willReturn(departments);

            // when
            List<DepartmentResponse> result = departmentUseCase.getDepartmentsByCampus("GLOBAL_CAMPUS");

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(r -> r.campus() == Campus.GLOBAL_CAMPUS);
        }

        @Test
        @DisplayName("MEDICAL_CAMPUS 조회 시 메디컬 캠퍼스 학과 목록만 반환된다")
        void MEDICAL_CAMPUS_조회_시_메디컬_캠퍼스_학과_목록_반환() {
            // given
            List<Department> departments = List.of(
                    Department.create("의학과", Campus.MEDICAL_CAMPUS)
            );
            given(departmentGetService.findAllByCampus(Campus.MEDICAL_CAMPUS)).willReturn(departments);

            // when
            List<DepartmentResponse> result = departmentUseCase.getDepartmentsByCampus("MEDICAL_CAMPUS");

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("의학과");
        }

        @Test
        @DisplayName("해당 캠퍼스에 학과가 없으면 빈 배열이 반환된다")
        void 해당_캠퍼스에_학과_없으면_빈_배열_반환() {
            // given
            given(departmentGetService.findAllByCampus(Campus.MEDICAL_CAMPUS)).willReturn(List.of());

            // when
            List<DepartmentResponse> result = departmentUseCase.getDepartmentsByCampus("MEDICAL_CAMPUS");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("유효하지 않은 campus 값이면 InvalidCampusException이 발생한다")
        void 유효하지_않은_campus_값이면_예외_발생() {
            // when & then
            assertThatThrownBy(() -> departmentUseCase.getDepartmentsByCampus("INVALID"))
                    .isInstanceOf(InvalidCampusException.class);
        }

        @Test
        @DisplayName("campus 값이 빈 문자열이면 InvalidCampusException이 발생한다")
        void campus_값이_빈_문자열이면_예외_발생() {
            // when & then
            assertThatThrownBy(() -> departmentUseCase.getDepartmentsByCampus(""))
                    .isInstanceOf(InvalidCampusException.class);
        }
    }
}
