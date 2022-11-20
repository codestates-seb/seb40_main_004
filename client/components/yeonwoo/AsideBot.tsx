/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

export const AsideBot = () => {
  return (
    <div className="mt-[74px]">
      <div>
        <span className="text-2xl font-semibold">기술 태그</span>
      </div>
      <div>
        <div className="mt-6">
          <button className="bg-main-yellow rounded-full py-[6px] w-32">
            React
          </button>
        </div>
        <div className="my-4">
          <button className="bg-main-yellow rounded-full py-[6px] w-32">
            HTML
          </button>
        </div>
        <div>
          <button className="bg-main-yellow rounded-full py-[6px] w-32">
            CSS
          </button>
        </div>
      </div>
    </div>
  );
};
