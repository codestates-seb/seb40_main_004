/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-02(박혜정)
 */

import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { QuestionList } from '../../components/haseung/QuestionList';
import { SearchWithTagButton } from '../../components/haseung/SearchWithTagButton';
import { Footer } from '../../components/common/Footer';
import { useState } from 'react';
import { useFetch } from '../../libs/useFetchSWR';
import { useForm, SubmitHandler } from 'react-hook-form';
import Link from 'next/link';
import { faChevronDown } from '@fortawesome/free-solid-svg-icons';

type FormValue = {
  keyword: string;
};

const Questions: NextPage = () => {
  const [pageIndex, setPageIndex] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [sort, setSort] = useState(['최신순', 'desc']);

  const { data: response, isLoading } = useFetch(
    `/api/articles?page=${pageIndex}&size=10&category=QNA&target=titleAndContent&keyword=${keyword}&sort=${sort[1]}`,
  );

  const { register, handleSubmit } = useForm<FormValue>();
  const onValid: SubmitHandler<FormValue> = (data) => {
    setKeyword(data.keyword);
  };

  const [isOpen, setIsOpen] = useState(false);
  const dropdownSortArr = [
    '최신순',
    '오래된순',
    '좋아요순',
    '답변많은순',
    '채택완료',
    '미채택',
  ];
  const handleSort = (e: React.MouseEvent<HTMLElement>) => {
    const text = (e.target as HTMLLIElement).textContent;
    switch (text) {
      case '최신순':
        setSort([text, 'desc']);
        break;
      case '오래된순':
        setSort([text, 'asc']);
        break;
      case '좋아요순':
        setSort([text, 'like-desc']);
        break;
      case '답변많은순':
        setSort([text, 'answer-desc']);
        break;
      case '채택완료':
        setSort([text, 'isChecked-true-desc']);
        break;
      case '미채택':
        setSort([text, 'isChecked-false-desc']);
        break;
    }
    setIsOpen(false);
  };

  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto flex space-x-5 p-8 md:p-16 bg-white shadow-sm border-[1px] border-gray-200">
        <form
          className="hidden md:flex flex-col w-full items-center justify-center"
          onSubmit={handleSubmit(onValid)}
        >
          <section className="w-full flex justify-center items-center">
            <div>
              <FontAwesomeIcon
                icon={faMagnifyingGlass}
                className="relative left-6"
              />
            </div>
            <input
              type="text"
              className="justify-center border border-solid border-main-gray rounded-md mr-2 py-1 bg-gray-50 w-64 pl-7 pr-2 placeholder:text-sm"
              placeholder="검색어를 입력해주세요."
              {...register('keyword')}
            />
            <button className="bg-main-yellow bg-opacity-80 py-1.5 px-4 rounded-md text-font-gray hover:bg-main-yellow hover:opacity-100 transition-all">
              검색
            </button>
          </section>
          <SearchWithTagButton />
        </form>
        <section className="flex flex-col w-full space-y-6">
          <article className="flex justify-between">
            <div className="flex flex-col relative">
              <button
                className="border border-main-gray rounded-lg py-1.5 w-28"
                onClick={() => setIsOpen((prev) => !prev)}
              >
                {`${sort[0]} `}
                <FontAwesomeIcon icon={faChevronDown} className="fa-xs" />
              </button>
              {isOpen ? (
                <ul className="bg-white w-full font-bold shadow-md cursor-pointer absolute top-11">
                  {dropdownSortArr.map((value) => {
                    return (
                      <li
                        value={value}
                        key={value}
                        className="p-4 hover:bg-main-yellow transition-all text-center"
                        onClick={handleSort}
                      >
                        {value}
                      </li>
                    );
                  })}
                </ul>
              ) : null}
            </div>
            <Link href="/ask">
              <button className="bg-main-orange bg-opacity-80 py-1.5 px-4 rounded-md text-font-gray hover:bg-main-orange hover:opacity-100 w-28 transition-all">
                질문하기
              </button>
            </Link>
          </article>
          <QuestionList
            response={response}
            pageIndex={pageIndex}
            setPageIndex={setPageIndex}
            isLoading={isLoading}
          />
        </section>
      </main>
      <Footer />
    </>
  );
};

export default Questions;
