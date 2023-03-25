/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-18
 */

import * as React from 'react';
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { wrap } from 'popmotion';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronLeft,
  faChevronRight,
} from '@fortawesome/free-solid-svg-icons';

const images = ['morak-main-1.png', 'morak-main-2.png', 'morak-main-3.png'];

const variants = {
  enter: (direction: number) => {
    return {
      x: direction > 0 ? 1 : -1,
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
      x: direction < 0 ? 1 : -1,
      opacity: 0,
    };
  },
};

const swipeConfidenceThreshold = 10000;
const swipePower = (offset: number, velocity: number) => {
  return Math.abs(offset) * velocity;
};

export const CarouselMain = () => {
  const [[page, direction], setPage] = useState([0, 0]);

  const imageIndex = wrap(0, images.length, page);

  const paginate = (newDirection: number) => {
    setPage([page + newDirection, newDirection]);
  };

  useEffect(() => {
    const timer = setInterval(() => {
      paginate(1);
    }, 3000);

    return () => {
      clearInterval(timer);
    };
  }, [page]);

  return (
    <>
      <AnimatePresence initial={false} custom={direction}>
        <motion.img
          key={page}
          className="min-w-[1280px] h-[350px] absolute"
          src={images[imageIndex]}
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
              paginate(1);
            } else if (swipe > swipeConfidenceThreshold) {
              paginate(-1);
            }
          }}
        />
      </AnimatePresence>
      <div
        className="absolute top-[50% - 20px] z-10 right-8 hover:cursor-pointer"
        onClick={() => paginate(1)}
      >
        <FontAwesomeIcon icon={faChevronRight} size="4x" />
      </div>
      <div
        className="absolute top-[50% - 20px] z-10 left-8 hover:cursor-pointer"
        onClick={() => paginate(-1)}
      >
        <FontAwesomeIcon icon={faChevronLeft} size="4x" />
      </div>
    </>
  );
};
