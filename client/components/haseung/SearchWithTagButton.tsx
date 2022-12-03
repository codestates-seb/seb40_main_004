/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-29
 */

import { BtnTag } from '../hyejung/BtnTag';

export const SearchWithTagButton = () => {
  return (
    <article className="flex flex-col mt-8">
      {[...Array(3)].map(() => (
        <section className="space-x-2 space-y-2 ml-14 p-4">
          <BtnTag children="label" />
          <BtnTag children="label" />
          <BtnTag children="label" />
        </section>
      ))}
    </article>
  );
};
