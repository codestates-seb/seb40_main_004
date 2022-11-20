/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-20
 */
import { Button } from '../../common/Button';
export const TextArea = () => {
  return (
    <form className="flex flex-col space-y-3">
      <textarea
        className="w-full rounded-[20px] border p-4 text-sm  focus:outline-main-gray"
        rows={4}
      />
      <div className="flex justify-end">
        <Button>코멘트 등록</Button>
      </div>
    </form>
  );
};
