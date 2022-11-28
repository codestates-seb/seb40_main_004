/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import * as React from 'react';
import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { wrap } from 'popmotion';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronLeft,
  faChevronRight,
  faComment,
  faHeart,
} from '@fortawesome/free-solid-svg-icons';

const articles = [
  {
    title:
      '리액트 쿼리 질문드립니다!!리액트 쿼리 질문드립니다!!리액트 쿼리 질문드립니다!!리액트 쿼리 질문드립니다!!',
    text: '안녕하세요 매번 제로초님의 강의로 많은 도움과 영감을 얻어 사내에서 사용하는 시스템을 적용중에 있습니다. 감사드립니다. 다름이 아니라 타입스크립트 버전인 리액트쿼리를 깃허브에 올려주셔서 적용해봤는데요 새로고침 시 기존에는 깜박임 없이 바로 로그인 이후 페이지로 연결되었던게 지금은 일시적으로 로그인 페이지에 접근했다가 메인페이지로 가더라구요 그래서 임시로 Loading아이콘을 보여주는 식으로 했는데 제가 잘못사용해서 그런건가요? 소스코드 첨부합니다.ㅜㅜ',
    createdAt: 1668878420020,
    like: 13,
    comment: 1,
  },
  {
    title: '리액트 쿼리 질문드립니다!!',
    text: '안녕하세요 매번 제로초님의 강의로 많은 도움과 영감을 얻어 사내에서 사용하는 시스템을 적용중에 있습니다. 감사드립니다. 다름이 아니라 타입스크립트 버전인 리액트쿼리를 깃허브에 올려주셔서 적용해봤는데요 새로고침 시 기존에는 깜박임 없이 바로 로그인 이후 페이지로 연결되었던게 지금은 일시적으로 로그인 페이지에 접근했다가 메인페이지로 가더라구요 그래서 임시로 Loading아이콘을 보여주는 식으로 했는데 제가 잘못사용해서 그런건가요? 소스코드 첨부합니다.ㅜㅜ',
    createdAt: 1668878420020,
    like: 13,
    comment: 2,
  },
  {
    title: '리액트 쿼리 질문드립니다!!',
    text: '안녕하세요 매번 제로초님의 강의로 많은 도움과 영감을 얻어 사내에서 사용하는 시스템을 적용중에 있습니다. 감사드립니다. 다름이 아니라 타입스크립트 버전인 리액트쿼리를 깃허브에 올려주셔서 적용해봤는데요 새로고침 시 기존에는 깜박임 없이 바로 로그인 이후 페이지로 연결되었던게 지금은 일시적으로 로그인 페이지에 접근했다가 메인페이지로 가더라구요 그래서 임시로 Loading아이콘을 보여주는 식으로 했는데 제가 잘못사용해서 그런건가요? 소스코드 첨부합니다.ㅜㅜ',
    createdAt: 1668878420020,
    like: 13,
    comment: 3,
  },
  {
    title: '리액트 쿼리 질문드립니다!!',
    text: '안녕하세요 매번 제로초님의 강의로 많은 도움과 영감을 얻어 사내에서 사용하는 시스템을 적용중에 있습니다. 감사드립니다. 다름이 아니라 타입스크립트 버전인 리액트쿼리를 깃허브에 올려주셔서 적용해봤는데요 새로고침 시 기존에는 깜박임 없이 바로 로그인 이후 페이지로 연결되었던게 지금은 일시적으로 로그인 페이지에 접근했다가 메인페이지로 가더라구요 그래서 임시로 Loading아이콘을 보여주는 식으로 했는데 제가 잘못사용해서 그런건가요? 소스코드 첨부합니다.ㅜㅜ',
    createdAt: 1668878420020,
    like: 13,
    comment: 4,
  },
  {
    title: '리액트 쿼리 질문드립니다!!',
    text: '안녕하세요 매번 제로초님의 강의로 많은 도움과 영감을 얻어 사내에서 사용하는 시스템을 적용중에 있습니다. 감사드립니다. 다름이 아니라 타입스크립트 버전인 리액트쿼리를 깃허브에 올려주셔서 적용해봤는데요 새로고침 시 기존에는 깜박임 없이 바로 로그인 이후 페이지로 연결되었던게 지금은 일시적으로 로그인 페이지에 접근했다가 메인페이지로 가더라구요 그래서 임시로 Loading아이콘을 보여주는 식으로 했는데 제가 잘못사용해서 그런건가요? 소스코드 첨부합니다.ㅜㅜ',
    createdAt: 1668878420020,
    like: 13,
    comment: 5,
  },
];

const variants = {
  enter: (direction: number) => {
    return {
      x: direction > 0 ? 1000 : -1000,
      opacity: 0,
    };
  },
  center: {
    zIndex: 1,
    x: 0,
    opacity: 1,
  },
  exit: (direction: number) => {
    return {
      zIndex: 0,
      x: direction < 0 ? 1000 : -1000,
      opacity: 0,
    };
  },
};

/**
 * Experimenting with distilling swipe offset and velocity into a single variable, so the
 * less distance a user has swiped, the more velocity they need to register as a swipe.
 * Should accomodate longer swipes and short flicks without having binary checks on
 * just distance thresholds and velocity > 0.
 */
const swipeConfidenceThreshold = 10000;
const swipePower = (offset: number, velocity: number) => {
  return Math.abs(offset) * velocity;
};

export const CarouselArticle = () => {
  const [[page, direction], setPage] = useState([0, 0]);

  // We only have 3 images, but we paginate them absolutely (ie 1, 2, 3, 4, 5...) and
  // then wrap that within 0-2 to find our image ID in the array below. By passing an
  // absolute page index as the `motion` component's `key` prop, `AnimatePresence` will
  // detect it as an entirely new image. So you can infinitely paginate as few as 1 images.
  const articleIndex = wrap(0, Math.ceil(articles.length / 2) * 2, page);

  const paginate = (newDirection: number) => {
    setPage([page + newDirection, newDirection]);
  };

  return (
    <div className="relative w-full h-[440px]">
      <AnimatePresence initial={false} custom={direction}>
        <motion.div
          key={page}
          className="w-full h-[440px] absolute px-24 flex-col"
          custom={direction}
          variants={variants}
          initial="enter"
          animate="center"
          exit="exit"
          transition={{
            x: { type: 'spring', stiffness: 300, damping: 30 },
          }}
          drag="x"
          dragConstraints={{ left: 0, right: 0 }}
          dragElastic={1}
          onDragEnd={(e, { offset, velocity }) => {
            const swipe = swipePower(offset.x, velocity.x);

            if (swipe < -swipeConfidenceThreshold) {
              paginate(2);
            } else if (swipe > swipeConfidenceThreshold) {
              paginate(-2);
            }
          }}
        >
          {articles.slice(articleIndex, articleIndex + 2).map((article) => (
            <motion.div
              key={article.comment}
              className="bg-main-yellow bg-opacity-20 w-[793px] h-[190px] rounded-2xl p-8 relative mb-[72px]"
            >
              <div className="flex justify-between items-start">
                <div>
                  <span className="text-2xl text-main-orange">Q. </span>
                  <span className="hover:cursor-pointer text-2xl">
                    {article.title.length > 30
                      ? `${article.title.slice(0, 30)}...`
                      : article.title}
                  </span>
                </div>
                <div className="flex gap-4">
                  <div className="flex gap-2">
                    <FontAwesomeIcon icon={faHeart} size="xs" />
                    <span className="text-xs">{article.like}</span>
                  </div>
                  <div className="flex gap-2">
                    <FontAwesomeIcon icon={faComment} size="xs" />
                    <span className="text-xs">{article.comment}</span>
                  </div>
                </div>
              </div>
              <div className="flex justify-end mb-2">
                <span className="text-[15px] text-main-gray">{`${new Date(
                  article.createdAt,
                ).getFullYear()}년 ${
                  new Date(article.createdAt).getMonth() + 1
                }월 ${new Date(article.createdAt).getDate()}일 ${
                  new Date(article.createdAt).getHours() < 12 ? '오전' : '오후'
                } ${
                  new Date(article.createdAt).getHours() > 12
                    ? new Date(article.createdAt).getHours() - 12
                    : new Date(article.createdAt).getHours()
                }시 ${new Date(article.createdAt).getMinutes()}분`}</span>
              </div>
              <div>
                <p className="hover:cursor-pointer">
                  {article.text.length > 180
                    ? `${article.text.slice(0, 180)}...`
                    : article.text}
                </p>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </AnimatePresence>
      <div
        className="absolute top-[45%] z-10 right-0 hover:cursor-pointer"
        onClick={() => paginate(2)}
      >
        <FontAwesomeIcon icon={faChevronRight} size="3x" />
      </div>
      <div
        className="absolute top-[45%] z-10 left-0 hover:cursor-pointer"
        onClick={() => paginate(-2)}
      >
        <FontAwesomeIcon icon={faChevronLeft} size="3x" />
      </div>
    </div>
  );
};