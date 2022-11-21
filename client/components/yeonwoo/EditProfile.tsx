/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-20
 */

export const EditProfileComponent = () => {
  return (
    <>
      <div className="mb-16">
        <span className="text-3xl font-bold">프로필 수정</span>
      </div>
      <form>
        <label htmlFor="nickname">닉네임</label>
        <input
          id="nickname"
          type="text"
          className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
        />
        <label htmlFor="message">메세지</label>
        <input
          id="message"
          type="text"
          className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
        />
        <label htmlFor="github">깃허브 주소</label>
        <input
          id="github"
          type="text"
          className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
        />
        <label htmlFor="blog">블로그 주소</label>
        <input
          id="blog"
          type="text"
          className="w-full rounded-full h-11 px-4 mt-2 mb-10 border border-main-gray"
        />
        <label htmlFor="userState">취준/현업/무관</label>
        <select
          id="userState"
          className="w-full rounded-full h-11 px-4 mt-2 mb-32 border border-main-gray"
        >
          <option value="student">개발자 취준생</option>
          <option value="developer">현업 개발자</option>
          <option value="citizen">개발에 관심있는 일반인</option>
        </select>
        <div className="flex gap-8">
          <button className="w-full py-[6px] rounded-full bg-main-yellow">
            저장
          </button>
          <button className="w-full py-[6px] rounded-full bg-main-gray">
            취소
          </button>
        </div>
      </form>
    </>
  );
};
