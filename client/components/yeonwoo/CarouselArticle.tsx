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
import { useRecoilValue } from 'recoil';
import { userDashboardAtom } from '../../atomsYW';
import Link from 'next/link';

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
  const { articles } = useRecoilValue(userDashboardAtom);
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
      {articles.length !== 0 ? (
        <>
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
                  key={article.articleId}
                  className="bg-main-yellow bg-opacity-20 w-[793px] h-[190px] rounded-2xl p-8 relative mb-[72px]"
                >
                  <div className="flex justify-between items-start">
                    <Link href={`/questions/${article.articleId}`}>
                      <div>
                        <span className="text-2xl text-main-orange">Q. </span>
                        <span className="hover:cursor-pointer text-2xl">
                          {article.title.length > 30
                            ? `${article.title.slice(0, 30)}...`
                            : article.title}
                        </span>
                      </div>
                    </Link>
                    <div className="flex gap-4">
                      <div className="flex gap-2">
                        <FontAwesomeIcon icon={faHeart} size="xs" />
                        <span className="text-xs">{article.likes}</span>
                      </div>
                      <div className="flex gap-2">
                        <FontAwesomeIcon icon={faComment} size="xs" />
                        <span className="text-xs">{article.commentCount}</span>
                      </div>
                    </div>
                  </div>
                  <div className="flex justify-end mb-2">
                    <span className="text-[15px] text-main-gray">{`${new Date(
                      article.createdAt,
                    ).getFullYear()}년 ${
                      new Date(article.createdAt).getMonth() + 1
                    }월 ${new Date(article.createdAt).getDate()}일 ${
                      new Date(article.createdAt).getHours() < 12
                        ? '오전'
                        : '오후'
                    } ${
                      new Date(article.createdAt).getHours() > 12
                        ? new Date(article.createdAt).getHours() - 12
                        : new Date(article.createdAt).getHours()
                    }시 ${new Date(article.createdAt).getMinutes()}분`}</span>
                  </div>
                  <div className="flex justify-end gap-4 items-end h-16">
                    {article.tags.map((tag) => (
                      <button
                        className="bg-main-yellow rounded-full py-[6px] w-32"
                        key={tag.tagId}
                      >
                        {tag.name}
                      </button>
                    ))}
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
        </>
      ) : (
        <div className="w-full h-full flex justify-center items-center">
          <span>등록된 나의 질문이 아직 없습니다</span>
        </div>
      )}
    </div>
  );
};
