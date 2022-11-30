/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-21
 */

type TitleProps = {
  title: string;
};

export const QuestionTitle = ({ title }: TitleProps) => {
  return (
    <section className="flex w-full text-2xl sm:text-3xl space-x-2">
      <h1 className="text-main-yellow font-semibold">Q.</h1>
      <h1>{title}</h1>
    </section>
  );
};
