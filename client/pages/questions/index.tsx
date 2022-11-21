/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { NextPage } from 'next';
import { Button } from '../../components/common/Button';
import { Header } from '../../components/common/Header';
import { LoadMoreButton } from '../../components/haseung/LoadMoreButton';
import { QuestionList } from '../../components/haseung/QuestionList';
import { SearchWithTagButton } from '../../components/haseung/SearchWithTagButton';

const Questions: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto">
        <h1>Questions</h1>
      </main>
      <section className="flex items-center ml-3">
        <form className="w-screen ml-96">
          <button>
            <FontAwesomeIcon
              icon={faMagnifyingGlass}
              className="relative left-6"
            />
          </button>
          <input
            type="text"
            className="w-[30%] justify-center mt-10 border border-solid border-font-gray rounded-xl mr-2"
          />
          <Button>검색</Button>
          <SearchWithTagButton />
          <LoadMoreButton />
        </form>
        <QuestionList />
      </section>
    </>
  );
};

export default Questions;
