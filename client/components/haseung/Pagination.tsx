/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-12-02
 * 최근 수정일: 2022-12-02
 */

import { useRecoilState } from 'recoil';
import { currPagesIndexAtom } from '../../atomsHJ';

type PaginationProps = {
  setPageIndex: any;
  totalPage: number;
  pageIndex: number;
};

const getTotalPagesArr = (totlaPageNum: number) => {
  const totalPagesArr = [];
  const totalPages = Array.from(
    { length: totlaPageNum },
    (_, idx: number) => idx + 1,
  );
  for (let i = 0; i < totalPages.length; i += 5) {
    totalPagesArr.push(totalPages.slice(i, i + 5));
  }
  return totalPagesArr;
};

export const Pagination = ({
  pageIndex,
  setPageIndex,
  totalPage,
}: PaginationProps) => {
  const handlePageChange = (e: React.MouseEvent<HTMLElement>) => {
    const value = (e.target as HTMLAnchorElement).text;
    setPageIndex(value);
  };

  const [currPagesIndex, setCurrPagesIndex] =
    useRecoilState(currPagesIndexAtom);
  const totalPagesGroup = getTotalPagesArr(totalPage);
  const currPagesGroup = totalPagesGroup[currPagesIndex];

  const handleChangePageNum = (e: React.MouseEvent<HTMLElement>) => {
    const text = (e.target as HTMLAnchorElement).text;
    if (text === '이전' && pageIndex > 5) {
      setCurrPagesIndex(currPagesIndex - 1);
      setPageIndex(pageIndex - 1);
    }
    if (text === '다음' && pageIndex < totalPagesGroup.length) {
      setCurrPagesIndex(currPagesIndex + 1);
      setPageIndex(currPagesGroup[4] + 1);
    }
  };
  return (
    <nav>
      <ul className="flex -space-x-px text-xs">
        <li>
          <a
            className="hidden lg:inline px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 rounded-l-lg cursor-pointer"
            onClick={() => {
              setCurrPagesIndex(0);
              setPageIndex(1);
            }}
          >
            처음으로
          </a>
        </li>
        <li>
          <a
            className="rounded-l-lg lg:rounded-none px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 cursor-pointer"
            onClick={handleChangePageNum}
          >
            이전
          </a>
        </li>
        {currPagesGroup.map((value) => (
          <li key={value}>
            <a
              className={`px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300  cursor-pointer  hover:text-gray-700 ${
                value === Number(pageIndex)
                  ? 'bg-main-yellow hover:bg-main-yellow'
                  : 'hover:bg-gray-100'
              }`}
              onClick={handlePageChange}
            >
              {value}
            </a>
          </li>
        ))}
        <li>
          <a
            className="rounded-r-lg lg:rounded-none px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 cursor-pointer"
            onClick={handleChangePageNum}
          >
            다음
          </a>
        </li>
        <li>
          <a
            className="hidden lg:inline px-3 py-2 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 rounded-r-lg cursor-pointer"
            onClick={() => {
              setCurrPagesIndex(totalPagesGroup.length - 1);
              setPageIndex(totalPage);
            }}
          >
            맨끝으로
          </a>
        </li>
      </ul>
    </nav>
  );
};
