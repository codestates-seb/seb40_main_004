/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import { faRefresh } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const LoadMoreButton = () => {
  return (
    <section className="flex mx-32 mt-10">
      <button className="bg-main-yellow text-[16px] px-5 py-[6px] rounded-full ">
        <span>더보기</span> <FontAwesomeIcon icon={faRefresh} />
      </button>
    </section>
  );
};
