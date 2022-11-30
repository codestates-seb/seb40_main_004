/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';

export const SearchBar = () => {
  return (
    <form className="w-full">
      <input
        type="text"
        className="w-[90%] border border-solid border-font-gray rounded-full pl-2"
      />
      <button>
        <FontAwesomeIcon
          icon={faMagnifyingGlass}
          className="relative -left-6"
        />
      </button>
    </form>
  );
};
