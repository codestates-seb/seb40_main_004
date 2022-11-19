/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const SearchWithTagButton = () => {
  return (
    <section className="flex mt-10 space-x-2">
      <button className="bg-main-yellow text-[16px] px-5 py-[6px] rounded-full">
        <FontAwesomeIcon icon={faPlus} /> <span>label</span>
      </button>
      <button className="bg-main-gray text-[16px] px-5 py-[6px] rounded-full">
        <FontAwesomeIcon icon={faPlus} /> <span>label</span>
      </button>
      <button className="bg-main-gray text-[16px] px-5 py-[6px] rounded-full">
        <FontAwesomeIcon icon={faPlus} /> <span>label</span>
      </button>
      <button className="bg-main-yellow text-[16px] px-5 py-[6px] rounded-full">
        <FontAwesomeIcon icon={faPlus} /> <span>label</span>
      </button>
    </section>
  );
};
