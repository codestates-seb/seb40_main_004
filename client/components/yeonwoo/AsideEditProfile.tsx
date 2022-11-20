/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-20
 */

import { faUser } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const AsideEditProfile = () => {
  return (
    <>
      <div className="w-full px-4 py-3 flex items-center bg-[#D9D9D9] border-l-4 border-main-yellow">
        <FontAwesomeIcon icon={faUser} size="lg" />
        <span className="ml-4 text-xl">프로필 수정</span>
      </div>
      <div className="w-full px-4 py-3 flex items-center border-l-4 border-transparent">
        <FontAwesomeIcon icon={faUser} size="lg" />
        <span className="ml-4 text-xl">비밀번호 수정</span>
      </div>
      <div className="w-full px-4 py-3 flex items-center border-l-4 border-transparent">
        <FontAwesomeIcon icon={faUser} size="lg" />
        <span className="ml-4 text-xl">회원탈퇴</span>
      </div>
    </>
  );
};
