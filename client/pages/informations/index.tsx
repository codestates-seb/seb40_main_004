import { faMagnifyingGlass } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { GetServerSideProps, NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { SearchWithTagButton } from '../../components/questions/SearchWithTagButton';
import { Footer } from '../../components/common/Footer';
import { useEffect, useState } from 'react';
import { useFetch } from '../../libs/useFetchSWR';
import { useForm, SubmitHandler } from 'react-hook-form';
import Link from 'next/link';
import { faChevronDown } from '@fortawesome/free-solid-svg-icons';
import { useRecoilState } from 'recoil';
import { keywordAtom } from '../../atomsYW';
import { useCheckClickIsLogin } from '../../libs/useCheckIsLogin';
import { Seo } from '../../components/common/Seo';
import { InformsList } from '../../components/informations/InformsList';

type FormValue = {
  keyword: string;
};

const Questions: NextPage = () => {
  const [pageIndex, setPageIndex] = useState(1);
  const [keyword, setKeyword] = useRecoilState(keywordAtom);
  const [sort, setSort] = useState(['최신순', 'desc']);
  const [isOpen, setIsOpen] = useState(false);
  const [target, setTarget] = useState('titleAndContent');

  useEffect(() => {
    setPageIndex(1);
    setKeyword('');
    setSort(['최신순', 'desc']);
    setTarget('titleAndContent');
  }, []);

  const { data: response, isLoading } = useFetch(
    `/api/articles?page=${pageIndex}&size=10&category=INFO&target=${target}&keyword=${keyword}&sort=${sort[1]}`,
  );

  const { register, handleSubmit } = useForm<FormValue>();
  const onValid: SubmitHandler<FormValue> = (data) => {
    setTarget('titleAndContent');
    if (!data.keyword.length) {
      setKeyword('');
      setKeyword('');
    } else {
      setKeyword(data.keyword);
    }
  };

  const dropdownSortArr = ['최신순', '오래된순', '좋아요순', '댓글많은순'];
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
      case '댓글많은순':
        setSort([text, 'comment-desc']);
        break;
    }
    setIsOpen(false);
  };
  const checkIsLogin = useCheckClickIsLogin();

  return (
    <>
      <Seo title="질문/답변" />
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
          <SearchWithTagButton
            setKeyword={setKeyword}
            setTarget={setTarget}
            keyword={keyword}
          />
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
              <button
                className="bg-main-orange bg-opacity-80 py-1.5 px-4 rounded-md text-font-gray hover:bg-main-orange hover:opacity-100 w-28 transition-all"
                onClick={checkIsLogin}
              >
                질문하기
              </button>
            </Link>
          </article>
          <InformsList
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

export const getServerSideProps: GetServerSideProps = async (context) => {
  const content = context.req.url?.split('/')[1];
  return {
    props: {
      content,
    },
  };
};

export default Questions;
