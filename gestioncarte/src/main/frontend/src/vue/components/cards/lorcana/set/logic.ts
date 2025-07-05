import {getTranslationField} from "@/localization";
import {LorcanaSetDTO, LorcanaSetTranslationDTO} from "@/types";

export const getSetName = (set?: LorcanaSetDTO): string => getTranslationField<LorcanaSetTranslationDTO, "name">(set?.translations, "name")?.[1] || '!!! EXTENSION SANS NOM !!!';
