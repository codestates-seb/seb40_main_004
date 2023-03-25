import { options } from '@libs/tagOptions';
import { SetterOrUpdater } from 'recoil';

type KeyProps = {
  setKeyword: SetterOrUpdater<string>;
  setTarget: React.Dispatch<React.SetStateAction<string>>;
  keyword: string;
};

const getTagList = () => {
  const tagList = [];
  for (let i = 0; i < options.length; i += 3) {
    tagList.push(options.slice(i, i + 3));
  }
  return tagList;
};

export const SearchWithTagButton = ({
  setKeyword,
  setTarget,
  keyword,
}: any) => {
  const tagList = getTagList();
  return (
    <section className="flex flex-col mt-8 justify-center space-y-6">
      {tagList.map((tags, i) => (
        <div className="flex justify-center space-x-4" key={i}>
          {tags.map((tag) => (
            <div
              key={tag.tagId}
              className={`px-4 py-1 min-w-[80px] hover:bg-opacity-100 transition rounded-full text-center cursor-pointer ${
                keyword === tag.name
                  ? 'bg-main-orange bg-opacity-80'
                  : 'bg-main-yellow  bg-opacity-50'
              }`}
              onClick={() => {
                setKeyword(tag.name);
                setTarget('tag');
              }}
            >
              {tag.name}
            </div>
          ))}
        </div>
      ))}
    </section>
  );
};
