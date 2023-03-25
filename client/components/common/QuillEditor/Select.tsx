import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useEffect, useState } from 'react';

export type SelectOption = {
  name: string;
  tagId?: number;
};

type MultipleSelectProps = {
  multiple: true;
  tags: SelectOption[];
  onChange: (value: SelectOption[]) => void;
};

type SingleSelectProps = {
  multiple?: false;
  tags?: SelectOption;
  onChange: (value: SelectOption | undefined) => void;
};

type SelectProps = {
  options: SelectOption[];
} & (SingleSelectProps | MultipleSelectProps);

export const Select = ({ multiple, tags, onChange, options }: SelectProps) => {
  const [isOpen, setIsOpen] = useState(true);
  const [highlightedIndex, setHighlightIndex] = useState(0);

  // tags 배열이 option을 포함하고 있으면, option을 제거한 새로운 배열 반환,
  // 아니면 option을 추가한 새로운 배열 반환
  const toggleTag = (option: SelectOption, tags: SelectOption[]) => {
    return tags?.includes(option)
      ? tags.filter((tag) => tag !== option)
      : [...tags, option];
  };

  const selectOption = (option: SelectOption) => {
    if (multiple) {
      const updatedTags = toggleTag(option, tags);
      onChange(updatedTags);
    } else if (option !== tags) onChange(option);
  };

  const isOptionSelected = (option: SelectOption) => {
    return multiple ? tags.includes(option) : option === tags;
  };

  useEffect(() => {
    if (isOpen) setHighlightIndex(0);
  }, [isOpen]);

  return (
    <div
      onClick={() => setIsOpen((prev) => !prev)}
      tabIndex={0}
      className="relative h-12 flex justify-start border-[0.05em] border-solid border-main-gray rounded-md outline-none cursor-pointer hover:border-main-orange"
    >
      <span className="flex-grow flex gap-2 flex-wrap">
        {multiple
          ? tags.map((tag) => (
              <button
                key={tag.tagId}
                onClick={(e) => {
                  e.stopPropagation();
                  selectOption(tag);
                }}
                className="rounded-md font-bold flex items-center rouned-sm my-2 py-1 px-1 mx-1 gap-1 cursor-pointer bg-transparent outline-none bg-main-orange hover:bg-main-yellow hover:border-main-gray focus:bg-main-yellow focus:border-main-yellow"
              >
                {tag.name}
                <span className="hover:text-main-gray">&times;</span>
              </button>
            ))
          : tags?.name}
      </span>
      {isOpen ? (
        <FontAwesomeIcon className="mt-4 mr-2" icon={faChevronDown} />
      ) : (
        <FontAwesomeIcon className="mt-4 mr-2" icon={faChevronUp} />
      )}
      <ul
        className={`absolute my-1 p-0 list-none max-h-[15em] overflow-y-auto border-solid border-[0.05rem] border-slate-800 w-full left-0 top-[calc(100%+0.25em)] bg-white z-[100] cursor-pointer block ${
          isOpen
            ? 'absolute m-0 p-0 list-none max-h-[15em] overflow-y-auto border-solid border-[0.05rem] border-slate-800 rounded-sm w-full left-0 top-[calc(100%+0.25em)] bg-white z-[100] px-2 py-1 cursor-pointer hidden'
            : ''
        }`}
      >
        {options.map((option, index) => (
          <li
            onClick={(event) => {
              event.stopPropagation();
              selectOption(option);
              setIsOpen(false);
            }}
            key={option.tagId}
            onMouseEnter={() => setHighlightIndex(index)}
            className={`py-1 cursor-pointer font-bold hover:bg-main-yellow transition-all px-2  ${
              isOptionSelected(option) ? 'bg-main-yellow' : ''
            } ${index === highlightedIndex ? 'bg-main-yellow text-white' : ''}`}
          >
            {option.name}
          </li>
        ))}
      </ul>
    </div>
  );
};
