/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 * 개요: 구분선을 표시합니다.
 */

export const Divider = () => {
  return (
    <section className="relative flex py-5 items-center w-full">
      <article className="flex-grow border-t border-gray-400"></article>
      <span className="flex-shrink mx-4 text-gray-400">소셜 로그인</span>
      <article className="flex-grow border-t border-gray-400"></article>
    </section>
  );
};
