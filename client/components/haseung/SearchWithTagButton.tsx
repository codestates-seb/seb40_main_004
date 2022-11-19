/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { TagButton } from '../hyejung/TagButton';

export const SearchWithTagButton = () => {
  return (
    <article className="flex flex-col mt-8">
      {[...Array(3)].map(() => (
        <section className="space-x-2 space-y-2 ml-14 p-4">
          <TagButton children="label" />
          <TagButton children="label" />
          <TagButton children="label" />
        </section>
      ))}
    </article>
  );
};
