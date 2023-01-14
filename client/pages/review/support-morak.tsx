import { NextPage } from 'next';
import { useRouter } from 'next/router';
import Link from 'next/link';
import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronRight,
  faChevronDown,
} from '@fortawesome/free-solid-svg-icons';
import { useRecoilValue, useRecoilState } from 'recoil';
import {
  reviewRequestAtom,
  reviewTagsAtom,
  reviewPointAtom,
} from '../../atoms/reviewAtom';
import { ProgressBar } from '../../components/review/ProgressBar';
import { BtnBackArticle } from '../../components/review/BtnBackArticle';
import { client } from '../../libs/client';
import { Seo } from '../../components/common/Seo';

const SupportMorak: NextPage = () => {
  const router = useRouter();
  const reviewTags = useRecoilValue(reviewTagsAtom);
  const [myPoints, setMyPoints] = useState(0);

  useEffect(() => {
    if (reviewTags?.length === 0) router.replace('/review');
    client
      .get(`/api/users/points`)
      .then((res) => setMyPoints(res.data.point))
      .catch((err) => console.log(err));
  }, []);

  const reviewRequest = useRecoilValue(reviewRequestAtom);
  const [points, setPoints] = useRecoilState(reviewPointAtom);

  const [isOpen, setIsOpen] = useState(false);
  const onClickDropdown = () => {
    setIsOpen(!isOpen);
    setPoints(0);
  };

  const onClickSelectMorak = (e: React.MouseEvent<HTMLElement>) => {
    const value = Number((e.target as HTMLButtonElement).value);
    if (value > myPoints) {
      alert(`현재 보유하신 모락은 ${myPoints} 모락 입니다!`);
      setIsOpen(false);
      setPoints(0);
    } else {
      setPoints(value);
      setIsOpen(false);
    }
  };

  return (
    <>
      <Seo title="응원/후원 - 모락 전달" />
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={reviewRequest.articleId} />
        </section>
        <section className="flex justify-between h-full md:border-l sm:space-x-10">
          <ProgressBar pageNumber={2} />
          <section className="flex flex-col space-y-10 w-full">
            <article className="text-left space-y-2 flex flex-col">
              <h1 className="text-2xl font-bold text-right">
                ✨후원을 통해 <br className="sm:hidden" /> 마음을 표현해보세요!
              </h1>
              <span className="font-bold text-right text-sm sm:text-base">
                고마운 분에게 모락을 보내 마음을 표현해볼까요?
                <br className="lg:hidden" />
                <span className="text-main-gray">
                  {' '}
                  (선택사항이오니 다음 단계로 넘어가셔도 됩니다.)
                </span>
              </span>
              <span></span>
            </article>

            <section className="flex space-y-10 sm:space-y-0 sm:space-x-10 flex-col sm:flex-row items-center sm:items-start">
              <article className="h-10 w-full sm:w-[400px] flex flex-col items-center z-10 space-y-4">
                <button
                  className="flex items-center justify-between w-full bg-white p-3 shadow-md"
                  onClick={onClickDropdown}
                >
                  <span>{points ? `${points} 모락` : '선택하기'}</span>
                  <FontAwesomeIcon icon={faChevronDown} />
                </button>
                {isOpen ? (
                  <ul className="bg-white w-full font-bold shadow-md cursor-pointer">
                    {[100, 200, 300, 400, 500].map((value) => {
                      return (
                        <li
                          value={value}
                          key={value}
                          className="p-5 hover:bg-main-yellow transition-all"
                          onClick={onClickSelectMorak}
                        >
                          {value} 모락
                        </li>
                      );
                    })}
                  </ul>
                ) : null}
              </article>

              <section className="flex w-full p-6 h-[400px] bg-white rounded-[20px] justify-center flex-col relative">
                <span className="absolute top-10 right-10 font-bold">
                  나의 모락 : {myPoints} 모락
                </span>
                {points ? (
                  <div className="text-2xl flex flex-col items-center w-full space-y-5 lg:space-y-10">
                    <span>
                      <strong className="text-3xl lg:text-5xl">
                        ✨ {points} 모락
                      </strong>
                      을
                    </span>
                    <span>
                      <strong className="text-3xl lg:text-5xl">
                        {' '}
                        {reviewRequest.targetUserName}
                      </strong>
                      님에게 후원합니다.
                    </span>
                  </div>
                ) : (
                  <div className="text-lg lg:text-2xl flex flex-col items-center w-full space-y-5 lg:space-y-10">
                    <span>
                      <strong className="text-3xl lg:text-5xl">
                        {reviewRequest.targetUserName}
                      </strong>
                      님에게 후원을 보내보세요!
                    </span>
                  </div>
                )}
              </section>
            </section>

            <article className="ml-auto text-right space-x-3">
              <Link href={'/review/completion'}>
                <button className="text-base sm:text-lg font-bold">
                  다음 단계로!
                  <FontAwesomeIcon
                    icon={faChevronRight}
                    className="fa-lg mr-1"
                  />
                </button>
              </Link>
            </article>
          </section>
        </section>
      </main>
    </>
  );
};

export default SupportMorak;
