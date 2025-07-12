import {LorcanaSetDTO, Service} from "@/types";

export const lorcanaSetService = new Service<LorcanaSetDTO, string>('/api/cards/lorcana/sets');
