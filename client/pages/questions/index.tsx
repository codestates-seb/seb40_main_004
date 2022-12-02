/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-12-02(박혜정)
 */

import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { NextPage } from 'next';
import { Button } from '../../components/common/Button';
import { Header } from '../../components/common/Header';
import { LoadMoreButton } from '../../components/haseung/LoadMoreButton';
import { QuestionList } from '../../components/haseung/QuestionList';
import { SearchWithTagButton } from '../../components/haseung/SearchWithTagButton';
import { Footer } from '../../components/common/Footer';

const Questions: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto flex p-16 bg-white shadow-sm border-[1px] border-gray-200">
        <form className="w-full ml-10">
          <button>
            <FontAwesomeIcon
              icon={faMagnifyingGlass}
              className="relative left-6"
            />
          </button>
          <input
            type="text"
            className="w-[30%] justify-center border mt-36 border-solid border-font-gray rounded-xl mr-2"
          />
          <Button>검색</Button>
          <SearchWithTagButton />
          <LoadMoreButton />
        </form>
        <QuestionList />
      </main>
      <Footer />
    </>
  );
};

export default Questions;
